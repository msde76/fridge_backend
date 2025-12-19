package capstone.fridge.domain.fridge.api;

import capstone.fridge.domain.fridge.api.dto.AutoPlaceDtos;
import capstone.fridge.domain.fridge.api.dto.FridgeDtos;
import capstone.fridge.domain.fridge.service.FridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fridge")
public class FridgeController {

    private final FridgeService fridgeService;

    @PatchMapping
    public ResponseEntity<FridgeDtos.ListRes> list(@RequestHeader("X-MEMBER-ID") Long memberId) {
        return ResponseEntity.ok(fridgeService.list(memberId));
    }

    @PostMapping
    public ResponseEntity<FridgeDtos.IngredientRes> addManual(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @RequestBody FridgeDtos.AddIngredientReq req
    ) {
        return ResponseEntity.ok(fridgeService.addManual(memberId, req));
    }

    @PostMapping("/ocr")
    public ResponseEntity<FridgeDtos.OcrRes> ocr(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(fridgeService.ocrAndSave(memberId, image));
    }

    //auto-place 추가
    @PostMapping("/auto-place")
    public ResponseEntity<FridgeDtos.AutoPlaceRes> autoPlace(
            @RequestHeader("X-MEMBER-ID") Long memberId
    ) {
        return ResponseEntity.ok(fridgeService.autoPlace(memberId));
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long ingredientId
    ) {
        fridgeService.delete(memberId, ingredientId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/auto-place-llm")
    public ResponseEntity<AutoPlaceDtos.Res> autoPlaceLlm(@RequestHeader("X-MEMBER-ID") Long memberId) {
        return ResponseEntity.ok(fridgeService.autoPlaceByLlm(memberId));
    }

}
