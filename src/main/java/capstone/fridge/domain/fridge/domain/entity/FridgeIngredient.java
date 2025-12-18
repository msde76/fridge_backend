package capstone.fridge.domain.fridge.domain.entity;

import capstone.fridge.domain.member.domain.entity.Member;
import capstone.fridge.domain.model.entity.BaseTimeEntity;
import capstone.fridge.domain.model.enums.InputMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fridge_ingredient")
public class FridgeIngredient extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String name; // 재료명 (예: 두부)

    private String quantity; // 수량 (예: 1모, 300g)

    private LocalDate expiryDate; // 유통기한 [cite: 416]

    private String storageCompartment; // 카테고리 (육류, 채소 등)

    @Enumerated(EnumType.STRING)
    private InputMethod inputMethod; // MANUAL, OCR [cite: 413, 415]

    @Builder
    public FridgeIngredient(Member member, String name, String quantity, LocalDate expiryDate, String storageCompartment, InputMethod inputMethod) {
        this.member = member;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.storageCompartment = storageCompartment;
        this.inputMethod = inputMethod;
    }
}