package org.example.post2trip.domain.place.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.place.application.AIService;
import org.example.post2trip.domain.place.application.RecommendPlaceService;
import org.example.post2trip.domain.place.domain.RecommendPlace;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceResponseDto;
import org.example.post2trip.global.common.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "recommend", description = "여행 계획 세우기 api / 담당자 : 이영학")
public class GetPlaceController {

    private final AIService aiService;
    private final RecommendPlaceService recommendPlaceService;

  /*  @GetMapping("")
    public CompletableFuture<ResponseModel<?>> getPlace(@RequestParam(required = false) String url) {
        return aiService.getBusinessName(url)
                .thenApply(businessName -> ResponseModel.success(businessName)) // 성공 시
                .exceptionally(); // 실패 시
    }*/



    @GetMapping("")
    public ResponseEntity<List<RecommendPlace>> getAllRecommendPlaces() {
        return ResponseEntity.ok(recommendPlaceService.getAllRecommendPlaces());
    }

    // 🔹 2. 특정 추천 장소 조회 (ID 기준)
    @GetMapping("/place")
    public CompletableFuture<ResponseEntity<RecommendPlaceResponseDto>> getPlacesBySId(
            @RequestParam long sId,
            @RequestParam int days) {

        return recommendPlaceService.getRecommendPlacesBySId(sId, days)
                .thenApply(ResponseEntity::ok) // ✅ 성공 시 ResponseEntity로 감싸기
                .exceptionally(ex -> ResponseEntity.internalServerError().build()); // ✅ 실패 시 500 반환
    }


    // 🔹 3. 추천 장소 추가
    @PostMapping("")
    public ResponseEntity<RecommendPlace> createRecommendPlace(
            @RequestParam Long placeId,
            @RequestParam int days,
            @RequestParam int sort
    ) {
        return ResponseEntity.ok(recommendPlaceService.createRecommendPlace(placeId, days, sort));
    }

    // 🔹 4. 추천 장소 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecommendPlace> updateRecommendPlace(
            @PathVariable Long id,
            @RequestParam int days,
            @RequestParam int sort
    ) {
        return ResponseEntity.ok(recommendPlaceService.updateRecommendPlace(id, days, sort));
    }

    // 🔹 5. 추천 장소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendPlace(@PathVariable Long id) {
        recommendPlaceService.deleteRecommendPlace(id);
        return ResponseEntity.noContent().build();
    }
}
