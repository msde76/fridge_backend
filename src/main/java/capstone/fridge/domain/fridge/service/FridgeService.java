package capstone.fridge.domain.fridge.service;

import capstone.fridge.domain.fridge.api.dto.AutoPlaceDtos;
import capstone.fridge.domain.fridge.api.dto.FridgeDtos;
import capstone.fridge.domain.fridge.domain.entity.FridgeIngredient;
import capstone.fridge.domain.fridge.domain.enums.FridgeSlot;
import capstone.fridge.domain.fridge.domain.repository.FridgeIngredientRepository;
import capstone.fridge.domain.member.domain.entity.Member;
import capstone.fridge.domain.member.domain.repository.MemberRepository;
import capstone.fridge.domain.model.enums.InputMethod;
import capstone.fridge.global.client.FastApiClient;
import capstone.fridge.global.client.dto.FastApiPlaceDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FridgeService {

    private final MemberRepository memberRepository;
    private final FridgeIngredientRepository fridgeIngredientRepository;
    private final OcrService ocrService;
    private final FastApiClient fastApiClient;

    public FridgeDtos.IngredientRes addManual(Long memberId, FridgeDtos.AddIngredientReq req) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        FridgeIngredient ingredient = FridgeIngredient.builder()
                .member(member)
                .name(req.getName())
                .quantity(req.getQuantity())
                .expiryDate(req.getExpiryDate())
                .storageCategory(req.getStorageCategory())
                .inputMethod(InputMethod.MANUAL)
                .build();

        FridgeIngredient saved = fridgeIngredientRepository.save(ingredient);
        return toRes(saved);
    }

    @Transactional(readOnly = true)
    public FridgeDtos.ListRes list(Long memberId) {
        List<FridgeIngredient> items = fridgeIngredientRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
        return FridgeDtos.ListRes.builder()
                .items(items.stream().map(this::toRes).toList())
                .build();
    }

    public void delete(Long memberId, Long ingredientId) {
        FridgeIngredient ingredient = fridgeIngredientRepository.findByIdAndMemberId(ingredientId, memberId).orElseThrow();
        fridgeIngredientRepository.delete(ingredient);
    }

    public FridgeDtos.OcrRes ocrAndSave(Long memberId, MultipartFile image) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        List<OcrService.OcrItem> ocrItems = ocrService.extract(image);

        List<FridgeIngredient> saved = ocrItems.stream().map(it ->
                FridgeIngredient.builder()
                        .member(member)
                        .name(it.name())
                        .quantity(it.quantity())
                        .storageCategory(it.storageCategory())
                        .inputMethod(InputMethod.OCR)
                        .build()
        ).map(fridgeIngredientRepository::save).toList();

        return FridgeDtos.OcrRes.builder()
                .saved(saved.stream().map(this::toRes).toList())
                .build();
    }

    // ✅ 여기서 “룰 기반”으로 slot을 채움 (나중에 LLM으로 이 부분 교체)
    public FridgeDtos.AutoPlaceRes autoPlace(Long memberId) {
        memberRepository.findById(memberId).orElseThrow();

        List<FridgeIngredient> targets =
                fridgeIngredientRepository.findAllByMemberIdAndFridgeSlotIsNullOrderByCreatedAtDesc(memberId);

        List<FridgeIngredient> placed = new ArrayList<>();
        List<Long> unplaced = new ArrayList<>();

        for (FridgeIngredient ing : targets) {
            FridgeSlot slot = ruleBasedSlot(ing.getStorageCategory(), ing.getName());
            if (slot == null) {
                unplaced.add(ing.getId());
                continue;
            }
            ing.assignSlot(slot); //  dirty checking으로 저장됨
            placed.add(ing);
        }

        return FridgeDtos.AutoPlaceRes.builder()
                .placed(placed.stream().map(this::toRes).toList())
                .unplacedIds(unplaced)
                .build();
    }

    // 지금은 테스트용 룰, 나중에 여기 자리에 LLM 결과를 끼우면 됨
    private FridgeSlot ruleBasedSlot(String storageCategory, String name) {
        if (storageCategory == null) return null;

        return switch (storageCategory) {
            case "채소", "과일" -> FridgeSlot.CRISPER_DRAWER;
            case "육류", "해산물" -> FridgeSlot.MAIN_SHELF_3;
            case "유제품" -> FridgeSlot.MAIN_SHELF_2;
            case "소스", "양념", "음료" -> FridgeSlot.DOOR_SHELF_2;
            default -> null;
        };
    }

    private FridgeDtos.IngredientRes toRes(FridgeIngredient e) {
        return FridgeDtos.IngredientRes.builder()
                .id(e.getId())
                .name(e.getName())
                .quantity(e.getQuantity())
                .expiryDate(e.getExpiryDate())
                .storageCategory(e.getStorageCategory())
                .fridgeSlot(e.getFridgeSlot())
                .inputMethod(e.getInputMethod())
                .build();
    }
    @Transactional
    public AutoPlaceDtos.Res autoPlaceByLlm(Long memberId) {
        List<FridgeIngredient> items = fridgeIngredientRepository.findAllByMemberId(memberId);

        // FastAPI로 보낼 요청 바디 구성
        List<FastApiPlaceDtos.Item> reqItems = items.stream()
                .map(i -> new FastApiPlaceDtos.Item(
                        i.getId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getStorageCategory()
                ))
                .toList();

        FastApiPlaceDtos.PlaceRes fastRes =
                fastApiClient.place(new FastApiPlaceDtos.PlaceReq(reqItems));

        List<AutoPlaceDtos.PlacedItem> placed = new ArrayList<>();
        List<AutoPlaceDtos.UnplacedItem> unplaced = new ArrayList<>();

        // 1) 배치 성공: DB 업데이트 + 응답용 placed에 담기
        for (FastApiPlaceDtos.Placement p : fastRes.placements()) {
            FridgeIngredient ing = fridgeIngredientRepository.findById(p.id()).orElseThrow();

            // FastAPI가 enum string을 줬다고 가정 (ex: "MAIN_SHELF_2")
            FridgeSlot slot = FridgeSlot.valueOf(p.fridgeSlot());
            ing.assignSlot(slot);

            placed.add(new AutoPlaceDtos.PlacedItem(
                    ing.getId(),
                    ing.getName(),
                    ing.getStorageCategory(),
                    ing.getFridgeSlot().name()
            ));
        }

        // 2) 배치 실패: 응답용 unplaced에 담기
        for (FastApiPlaceDtos.Unplaced u : fastRes.unplaced()) {
            unplaced.add(new AutoPlaceDtos.UnplacedItem(
                    u.id(),
                    u.name(),
                    u.reason()
            ));
        }

        return new AutoPlaceDtos.Res(placed, unplaced);
    }


}

