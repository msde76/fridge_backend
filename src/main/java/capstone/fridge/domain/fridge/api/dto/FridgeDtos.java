package capstone.fridge.domain.fridge.api.dto;

import capstone.fridge.domain.fridge.domain.enums.FridgeSlot;
import capstone.fridge.domain.model.enums.InputMethod;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class FridgeDtos {

    @Getter @Setter
    public static class AddIngredientReq {
        private String name;
        private String quantity;
        private LocalDate expiryDate;
        private String storageCategory; // 분류(육류/채소/유제품/소스...)
    }

    @Getter @Builder
    public static class IngredientRes {
        private Long id;
        private String name;
        private String quantity;
        private LocalDate expiryDate;
        private String storageCategory;
        private FridgeSlot fridgeSlot;
        private InputMethod inputMethod;
    }

    @Getter @Builder
    public static class ListRes {
        private List<IngredientRes> items;
    }

    @Getter @Builder
    public static class OcrRes {
        private List<IngredientRes> saved;
    }

    @Getter @Builder
    public static class AutoPlaceRes {
        private List<IngredientRes> placed;
        private List<Long> unplacedIds;
    }
}
