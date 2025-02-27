package org.example.post2trip.domain.place.application;

import lombok.AllArgsConstructor;
import org.example.post2trip.domain.place.dao.PlaceRepository;
import org.example.post2trip.domain.place.dao.RecommendPlaceRepository;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.domain.RecommendPlace;
import org.example.post2trip.domain.place.dto.request.AI.AIRequestDto;
import org.example.post2trip.domain.place.dto.request.AI.AIPlaceDto;
import org.example.post2trip.domain.place.dto.response.AI.AIResponseDto;
import org.example.post2trip.domain.place.dto.response.PlaceReponseDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RecommendPlaceService {
    private final RestTemplate restTemplate;
    private final RecommendPlaceRepository recommendPlaceRepository;
    private final PlaceRepository placeRepository;

    private final AIService aiService;

    // 🔹 1. 모든 추천 장소 조회
    public List<RecommendPlace> getAllRecommendPlaces() {
        return recommendPlaceRepository.findAll();
    }

    // 🔹 2. 특정 추천 장소 조회 (ID 기준)
    @Async
    public CompletableFuture<RecommendPlaceResponseDto> getRecommendPlacesBySId(Long sId, int days) {
        // 1️⃣ sId를 사용하여 Place 리스트 조회
        List<Place> places = placeRepository.findBySidAndIsUsed(sId, true);
        System.out.println("🔹 조회된 places: " + places.stream().map(Place::getName).collect(Collectors.toList()));

        // 2️⃣ AI 서버 요청 데이터 생성
        AIRequestDto aiRequest = AIRequestDto.builder()
                .days(days)
                .places(places.stream()
                        .map(p -> AIPlaceDto.builder()
                                .id(p.getId())
                                .category(p.getCategory())
                                .place_name(p.getName())
                                .summary(p.getDescription())
                                .latitude(Double.parseDouble(p.getLatitude()))
                                .longitude(Double.parseDouble(p.getLongitude()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        // 3️⃣ AI 서버에 요청하여 응답 받기
        List<AIResponseDto> aiResponses = aiService.sendRequestToAIServer(aiRequest);
        System.out.println("🔹 AI 서버 응답: " + aiResponses);

        // 4️⃣ AI 응답을 `RecommendPlaceDto`로 변환 (place_name을 기준으로 매칭)
        List<RecommendPlaceDto> recommendPlaces = places.stream()
                .map(place -> {
                    // AI 응답에서 해당 `place_name`과 매칭되는 데이터 찾기
                    AIResponseDto aiResponse = aiResponses.stream()
                            .filter(ai -> ai.getPlaceName().equals(place.getName()))
                            .findFirst()
                            .orElse(null);

                    // AI 응답이 없는 경우 기본값 설정
                    int day = (aiResponse != null) ? aiResponse.getDay() : days;
                    int sort = (aiResponse != null) ? aiResponse.getSort() : 99; // 기본 sort 값 (가장 마지막에 정렬됨)

                    return RecommendPlaceDto.builder()
                            .days(day)
                            .sort(sort)
                            .place(PlaceReponseDto.builder()
                                    .name(place.getName())
                                    .basicAddress(place.getBasicAddress())
                                    .description(place.getDescription())
                                    .latitude(place.getLatitude())
                                    .longitude(place.getLongitude())
                                    .isUsed(place.isUsed())
                                    .imageUrl(place.getImageUrl())
                                    .url(place.getUrl())
                                    .build())
                            .build();
                })
                .sorted(Comparator.comparing(RecommendPlaceDto::getDays) // ✅ 1순위: days 오름차순
                        .thenComparing(RecommendPlaceDto::getSort)) // ✅ 2순위: sort 오름차순
                .collect(Collectors.toList());

        // 5️⃣ 최종 DTO 반환
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
