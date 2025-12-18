package capstone.fridge.domain.scrap.domain.repository;

import capstone.fridge.domain.scrap.domain.entity.RecipeScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeScrapRepository extends JpaRepository<RecipeScrap, Long> {
}
