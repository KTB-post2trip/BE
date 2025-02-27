package org.example.post2trip.domain.place.application;

import lombok.AllArgsConstructor;
import org.example.post2trip.domain.place.dao.PlaceRepository;
import org.example.post2trip.domain.place.dao.RecommendPlaceRepository;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.domain.RecommendPlace;
import org.example.post2trip.domain.place.dto.response.PlaceReponseDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceResponseDto;
import org.example.post2trip.global.common.ResponseModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RecommendPlaceService {

    private final RecommendPlaceRepository recommendPlaceRepository;
    private final PlaceRepository placeRepository;



    // 🔹 1. 모든 추천 장소 조회
    public List<RecommendPlace> getAllRecommendPlaces() {
        return recommendPlaceRepository.findAll();
    }

    // 🔹 2. 특정 추천 장소 조회 (ID 기준)
    @Async
    public CompletableFuture<RecommendPlaceResponseDto> getRecommendPlacesBySId(Long sId, int days) {
        // 1️⃣ sId를 사용하여 Place 리스트 조회
        List<Place> places = placeRepository.findBySidAndIsUsed(sId, true);

        // 2️⃣ 조회된 Place 리스트에서 각각의 RecommendPlace 가져오기 → DTO로 변환
        List<RecommendPlaceDto> recommendPlaces = places.stream()
                .map(place -> recommendPlaceRepository.findByPlace(place))
                .filter(Optional::isPresent) // ✅ 존재하는 RecommendPlace만 필터링
                .map(Optional::get)
                .map(rp -> RecommendPlaceDto.builder()
                        .days(rp.getDays())
                        .sort(rp.getSort())
                        .place(PlaceReponseDto.builder()
                                .name(rp.getPlace().getName()) // ✅ 필드명 변경
                                .basicAddress(rp.getPlace().getBasicAddress())
                                .description(rp.getPlace().getDescription())
                                .latitude(rp.getPlace().getLatitude())
                                .longitude(rp.getPlace().getLongitude())
                                .isUsed(rp.getPlace().isUsed())
                                .imageUrl(rp.getPlace().getImageUrl())
                                .url(rp.getPlace().getUrl()) // ✅ 추가된 필드
                                .build())
                        .build())
                .sorted(Comparator.comparing(RecommendPlaceDto::getDays) // ✅ 1순위: days 오름차순
                        .thenComparing(RecommendPlaceDto::getSort)) // ✅ 2순위: sort 오름차순
                .collect(Collectors.toList());


        // 3️⃣ 최종 DTO 반환
        return CompletableFuture.completedFuture(RecommendPlaceResponseDto.builder()
                .places(recommendPlaces)
                .build());
    }




    // 🔹 3. 추천 장소 추가
    public RecommendPlace createRecommendPlace(Long placeId, int days, int sort) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found with id: " + placeId));

        RecommendPlace recommendPlace = RecommendPlace.builder()
                .place(place)
                .days(days)
                .sort(sort)
                .build();

        return recommendPlaceRepository.save(recommendPlace);
    }

    // 🔹 4. 추천 장소 수정
    public RecommendPlace updateRecommendPlace(Long id, int days, int sort) {
        return recommendPlaceRepository.findById(id)
                .map(existingRecommendPlace -> {
                    existingRecommendPlace.setDays(days);
                    existingRecommendPlace.setSort(sort);
                    return recommendPlaceRepository.save(existingRecommendPlace);
                }).orElseThrow(() -> new RuntimeException("RecommendPlace not found with id: " + id));
    }

    // 🔹 5. 추천 장소 삭제
    public void deleteRecommendPlace(Long id) {
        recommendPlaceRepository.deleteById(id);
    }
}
