package capstone.fridge.domain.recipe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recipe_step")
public class RecipeStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(nullable = false)
    private int stepOrder; // 순서 (1, 2...) [cite: 30]

    @Column(columnDefinition = "TEXT")
    private String content; // 설명 [cite: 31]

    private String imageUrl; // 과정 이미지

    @Column(columnDefinition = "TEXT")
    private String tip; // 요리 팁 (선택)

    @Builder
    public RecipeStep(Recipe recipe, int stepOrder, String content, String imageUrl, String tip) {
        this.recipe = recipe;
        this.stepOrder = stepOrder;
        this.content = content;
        this.imageUrl = imageUrl;
        this.tip = tip;
    }
}
