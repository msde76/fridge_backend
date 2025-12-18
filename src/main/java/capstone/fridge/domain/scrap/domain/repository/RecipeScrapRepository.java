package capstone.fridge.domain.scrap.domain.repository;

import capstone.fridge.domain.member.domain.entity.Member;
import capstone.fridge.domain.scrap.domain.entity.RecipeScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeScrapRepository extends JpaRepository<RecipeScrap, Long> {

    @Query("SELECT rs FROM RecipeScrap rs JOIN FETCH rs.recipe WHERE rs.member = :member")
    List<RecipeScrap> findAllByMemberWithRecipe(@Param("member") Member member);
}
