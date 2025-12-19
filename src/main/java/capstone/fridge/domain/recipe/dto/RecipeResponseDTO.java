package capstone.fridge.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecipeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeDTO {
        private Long recipeId;
        private String title;
        private String description;
        private String mainImage; // S3 URL
        private String cookTime;
        private String difficulty;
        private Integer servings;
        private List<String> missingIngredients;
    }
}
