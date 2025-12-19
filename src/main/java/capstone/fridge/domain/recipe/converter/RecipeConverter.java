package capstone.fridge.domain.recipe.converter;

import capstone.fridge.domain.recipe.domain.entity.Recipe;
import capstone.fridge.domain.recipe.dto.RecipeResponseDTO;

import java.util.Collections;
import java.util.List;

public class RecipeConverter {

    private static final String S3_BASE_URL = "https://capstone-fridge.s3.ap-northeast-2.amazonaws.com/recipes/";

    // 1. 부족한 재료가 없는 경우 (보유 재료 기반 추천용)
    public static RecipeResponseDTO.RecipeDTO toRecipeDTO(Recipe recipe) {
        return toRecipeDTO(recipe, Collections.emptyList());
    }

    // 2. 부족한 재료가 있는 경우 (부족한 재료 기반 추천용)
    public static RecipeResponseDTO.RecipeDTO toRecipeDTO(Recipe recipe, List<String> missingIngredients) {

        // 이미지 URL 생성
        String s3Url = S3_BASE_URL + recipe.getId() + "/main.png";

        return RecipeResponseDTO.RecipeDTO.builder()
                .recipeId(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .mainImage(s3Url)
                .difficulty(recipe.getDifficulty())
                .cookTime(recipe.getCookTime())
                .missingIngredients(missingIngredients)
                .build();
    }
}
