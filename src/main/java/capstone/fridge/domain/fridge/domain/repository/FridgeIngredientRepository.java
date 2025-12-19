package capstone.fridge.domain.fridge.domain.repository;

import capstone.fridge.domain.fridge.domain.entity.FridgeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {
    List<FridgeIngredient> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<FridgeIngredient> findByIdAndMemberId(Long id, Long memberId);
    List<FridgeIngredient> findAllByMemberIdAndFridgeSlotIsNullOrderByCreatedAtDesc(Long memberId);
    List<FridgeIngredient> findAllByMember_IdAndFridgeSlotIsNull(Long memberId);
    List<FridgeIngredient> findAllByMemberId(Long memberId);

}
