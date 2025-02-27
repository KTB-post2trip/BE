package org.example.post2trip.domain.place.application;

import lombok.AllArgsConstructor;
import org.example.post2trip.domain.place.dao.PlaceRepository;
import org.example.post2trip.domain.place.dao.RecommendPlaceRepository;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.domain.RecommendPlace;
import org.example.post2trip.domain.place.dto.request.AI.AIRequestDto;
import org.example.post2trip.domain.place.dto.request.AI.AIPlaceDto;
import org.example.post2trip.domain.place.dto.response.AI.AIResponseDto;
import org.example.post2trip.domain.place.dto.response.PlaceResponseDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceDto;
import org.example.post2trip.domain.place.dto.response.RecommendPlaceResponseDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
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
    public CompletableFuture<RecommendPlaceResponseDto> getRecommendPlacesBySId(String sId, int days) {
        // 1️⃣ sId를 사용하여 Place 리스트 조회
        List<Place> places = placeRepository.findBySidAndIsUsed(sId+"", true);
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
       /* if(aiResponses.isEmpty()) {
            List<RecommendPlace> recommendPlaces = recommendPlaceRepository.findAll();
            return CompletableFuture.completedFuture(RecommendPlaceResponseDto.builder()
                    .places(recommendPlaces.stream()
                            .map(recommendPlace -> RecommendPlaceDto.builder()
                                    .days(recommendPlace.getDays())
                                    .sort(recommendPlace.getSort())
                                    .place(PlaceReponseDto.builder()
                                            .name(recommendPlace.getPlace().getName())
                                            .basicAddress(recommendPlace.getPlace().getBasicAddress())
                                            .description(recommendPlace.getPlace().getDescription())
                                            .latitude(recommendPlace.getPlace().getLatitude())
                                            .longitude(recommendPlace.getPlace().getLongitude())
                                            .isUsed(recommendPlace.getPlace().isUsed())
                                            .imageUrl(recommendPlace.getPlace().getImageUrl())
                                            .url(recommendPlace.getPlace().getUrl())
                                            .build())
                                    .build())
                            .collect(Collectors.toList()))
                    .build());
        }*/
        Map<String, AIResponseDto> aiResponseMap = aiResponses.stream()
                .collect(Collectors.toMap(AIResponseDto::getPlaceName, Function.identity()));

        // 4️⃣ AI 응답을 `RecommendPlaceDto`로 변환 (place_name을 기준으로 매칭)
        List<RecommendPlaceDto> recommendPlaces = places.stream()
                .map(place -> {
                    AIResponseDto aiResponse = aiResponseMap.get(place.getName()); // ✅ O(1) 조회

                    int day = (aiResponse != null) ? aiResponse.getDay() : days;
                    int sort = (aiResponse != null) ? aiResponse.getSort() : 1;

                    return RecommendPlaceDto.builder()
                            .days(day)
                            .sort(sort)
                            .place(PlaceResponseDto.builder()
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
                .sorted(Comparator.comparing(RecommendPlaceDto::getDays)
                        .thenComparing(RecommendPlaceDto::getSort))
                .collect(Collectors.toList());

        // 7️⃣ 최종 결과 반환
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
