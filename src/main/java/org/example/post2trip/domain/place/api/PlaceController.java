package org.example.post2trip.domain.place.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.place.application.AIService;
import org.example.post2trip.domain.place.application.PlaceService;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.request.IdListRequestDto;
import org.example.post2trip.domain.place.dto.request.PlaceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
@Tag(name = "place", description = "장소 api / 담당자 : 이영학")
public class PlaceController {

    private final PlaceService placeService;

    private final AIService processUrlService;



    // 모든 장소 조회

    @GetMapping("/all")
    public ResponseEntity<List<Place>> getAllPlaces(@RequestParam String url,@RequestParam String placeName) {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }


    // 특정 ID로 장소 조회
    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sid/{sid}")
    public ResponseEntity<List<Place>> getPlaceBysId(@PathVariable String sid) {
        return ResponseEntity.ok(placeService.getPlaceBysId(sid));

    }

    // 장소 등록
    @PostMapping("")
    @Hidden
    public ResponseEntity<Place> createPlace(@RequestBody PlaceDto place) {
        placeService.createPlace(place);
        return ResponseEntity.ok().build();
    }

    // 장소 수정

    @PutMapping("")
    public ResponseEntity<Place> updatePlace(@RequestBody IdListRequestDto request) {
        placeService.updatePlaces(request.getIds());
        return ResponseEntity.ok().build();

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody Place place) {
        return ResponseEntity.ok(placeService.updatePlace(id, place));
    }

    // 장소 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public CompletableFuture<ResponseEntity<List<org.example.post2trip.domain.place.dto.response.AI.PlaceDto>>>processUrl(
            @RequestParam String url,
            @RequestParam(defaultValue = "강원") String placeName) {
        return processUrlService.processUrlAsync(url,placeName)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build()); // ✅ 실패 시 500 반환
    }




}
