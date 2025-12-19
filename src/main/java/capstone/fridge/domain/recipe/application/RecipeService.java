package capstone.fridge.domain.recipe.application;

import capstone.fridge.domain.recipe.dto.RecipeResponseDTO;

import java.util.List;

public interface RecipeService {

    List<RecipeResponseDTO.RecipeDTO> recommendRecipes(Long memberId);

    List<RecipeResponseDTO.RecipeDTO> recommendMissingRecipes(Long memberId);
}
