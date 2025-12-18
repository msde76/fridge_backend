package capstone.fridge.domain.fridge.domain.repository;

import capstone.fridge.domain.fridge.domain.entity.FridgeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {
}
