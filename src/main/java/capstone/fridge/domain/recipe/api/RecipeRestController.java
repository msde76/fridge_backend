package capstone.fridge.domain.recipe.api;

import capstone.fridge.domain.recipe.application.RecipeService;
import capstone.fridge.domain.recipe.dto.RecipeResponseDTO;
import capstone.fridge.global.common.response.BaseResponse;
import capstone.fridge.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeRestController {

    private final RecipeService recipeservice;

    @GetMapping("/recommend/fridge")
    @Operation(summary = "맞춤 레시피 추천 API", description = "사용자 냉장고의 재료를 기반으로 만들 수 있는 레시피를 추천")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "RECIPE_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    public BaseResponse<List<RecipeResponseDTO.RecipeDTO>> recommendRecipes(
            //@AuthenticationPrincipal PrincipalDetails principalDetails
            @RequestParam Long memberId
    ) {
        //MemberResponseDTO.UserInfoDTO result = memberService.getUserInfo(principalDetails.getMember().getId());
        List<RecipeResponseDTO.RecipeDTO> result = recipeservice.recommendRecipes(memberId);
        return BaseResponse.onSuccess(SuccessStatus.RECIPE, result);
    }

    @GetMapping("/recommend/fridge/missing")
    @Operation(summary = "부족 재료 기반 레시피 추천 API", description = "사용자 냉장고의 재료를 기반으로 조금의 재료를 추가하면 만들 수 있는 레시피를 추천")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "RECIPE_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    public BaseResponse<List<RecipeResponseDTO.RecipeDTO>> recommendMissingRecipes(
            //@AuthenticationPrincipal PrincipalDetails principalDetails
            @RequestParam Long memberId
    ) {
        //MemberResponseDTO.UserInfoDTO result = memberService.getUserInfo(principalDetails.getMember().getId());
        List<RecipeResponseDTO.RecipeDTO> result = recipeservice.recommendMissingRecipes(memberId);
        return BaseResponse.onSuccess(SuccessStatus.RECIPE, result);
    }
}
