package capstone.fridge.domain.recipe.domain.repository;

import capstone.fridge.domain.recipe.domain.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
