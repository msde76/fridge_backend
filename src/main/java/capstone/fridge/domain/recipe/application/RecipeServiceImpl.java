package capstone.fridge.domain.recipe.application;

import capstone.fridge.domain.fridge.domain.repository.FridgeIngredientRepository;
import capstone.fridge.domain.member.domain.entity.Member;
import capstone.fridge.domain.member.domain.entity.MemberPreference;
import capstone.fridge.domain.member.domain.repository.MemberPreferenceRepository;
import capstone.fridge.domain.member.domain.repository.MemberRepository;
import capstone.fridge.domain.recipe.converter.RecipeConverter;
import capstone.fridge.domain.recipe.domain.entity.Recipe;
import capstone.fridge.domain.recipe.domain.entity.RecipeIngredient;
import capstone.fridge.domain.recipe.domain.repository.RecipeRepository;
import capstone.fridge.domain.recipe.dto.RecipeResponseDTO;
import capstone.fridge.domain.recipe.exception.recipeException;
import capstone.fridge.global.error.code.status.ErrorStatus;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.Filter;
import io.qdrant.client.grpc.Points.Condition;
import io.qdrant.client.grpc.Points.FieldCondition;
import io.qdrant.client.grpc.Points.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FridgeIngredientRepository fridgeIngredientRepository;
    private final QdrantClient qdrantClient;
    private final EmbeddingService embeddingService;
    private final MemberRepository memberRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;

    @Override
    public List<RecipeResponseDTO.RecipeDTO> recommendRecipes(Long memberId) {

        // 1. 멤버 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new recipeException(ErrorStatus._MEMBER_NOT_FOUND));

        // 2. 사용자의 냉장고 재료 가져오기
        List<String> fridgeIngredients = fridgeIngredientRepository.findIngredientNamesByMemberId(member.getId());

        if (fridgeIngredients.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 제외할 재료(알레르기, 기피) 목록 조회
        List<MemberPreference> preferences = memberPreferenceRepository.findAllByMemberId(member.getId());
        List<String> excludedIngredients = preferences.stream()
                .map(MemberPreference::getIngredientName)
                .collect(Collectors.toList());

        // 빈 리스트가 들어가면 SQL IN 절에서 에러가 날 수 있으므로 더미 데이터나 처리 필요
        if (excludedIngredients.isEmpty()) {
            excludedIngredients.add(""); // 매칭되지 않을 임의의 값
        }

        // 4. DB 조회 (냉장고 재료로만 가능한 레시피 검색)
        List<Recipe> cookableRecipes = recipeRepository.findCookableRecipes(fridgeIngredients, excludedIngredients);

        // 5. Converter를 사용하여 DTO 변환 및 반환
        return cookableRecipes.stream()
                .map(RecipeConverter::toRecipeDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeResponseDTO.RecipeDTO> recommendMissingRecipes(Long memberId) {

        // 1. 멤버 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new recipeException(ErrorStatus._MEMBER_NOT_FOUND));

        // 2. 사용자의 냉장고 재료 가져오기
        List<String> ingredients = fridgeIngredientRepository.findIngredientNamesByMemberId(member.getId());

        if (ingredients.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 임베딩 생성 (검색어: 냉장고 재료)
        String queryText = String.join(" ", ingredients);
        List<Float> queryVector = embeddingService.getEmbedding(queryText);

        // 4. 제외할 재료(알레르기, 기피) 목록 조회
        List<MemberPreference> preferences = memberPreferenceRepository.findAllByMemberId(member.getId());
        List<String> excludedIngredients = preferences.stream()
                .map(MemberPreference::getIngredientName)
                .collect(Collectors.toList());

        // 5. Qdrant 필터 생성 (제외 로직)
        Filter.Builder filterBuilder = Filter.newBuilder();

        for (String excluded : excludedIngredients) {
            filterBuilder.addMustNot(Condition.newBuilder()
                    .setField(FieldCondition.newBuilder()
                            .setKey("ingredients") // Qdrant에 저장된 Payload Key
                            .setMatch(Match.newBuilder().setKeyword(excluded).build()) // 정확한 단어 매칭
                            .build())
                    .build());
        }
        Filter filter = filterBuilder.build();

        try {
            // 6. Qdrant 검색 (필터 적용)
            List<Points.ScoredPoint> searchResult = qdrantClient.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName("recipes")
                            .setFilter(filter) // [중요] 필터 적용
                            .addAllVector(queryVector)
                            .setLimit(5)
                            .build()
            ).get();

            // 7. 결과 ID 추출
            List<Long> recipeIds = searchResult.stream()
                    .map(point -> Long.parseLong(point.getId().getNum() + ""))
                    .collect(Collectors.toList());

            if (recipeIds.isEmpty()) {
                return Collections.emptyList();
            }

            // 8. MySQL 상세 조회 및 반환
            List<Recipe> recipes = recipeRepository.findAllById(recipeIds);

            return recipes.stream()
                    .map(recipe -> {
                        List<String> recipeIngredientNames = recipe.getIngredients().stream()
                                .map(RecipeIngredient::getName)
                                .collect(Collectors.toList());

                        List<String> missingIngredients = new ArrayList<>(recipeIngredientNames);
                        missingIngredients.removeAll(ingredients);

                        return RecipeConverter.toRecipeDTO(recipe, missingIngredients);
                    })
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Qdrant Search Failed: memberId={}", memberId, e);
            throw new recipeException(ErrorStatus._RECIPE_SEARCH_FAIL);
        }
    }
}
