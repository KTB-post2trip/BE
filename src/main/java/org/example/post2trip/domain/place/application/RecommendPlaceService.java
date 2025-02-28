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
import org.example.post2trip.domain.place.dto.response.ProcessUrlResponseDto;
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

    public RecommendPlaceDto convertToDto(RecommendPlace recommendPlace) {
        if (recommendPlace == null || recommendPlace.getPlace() == null) {
            return null;
        }

        Place place = recommendPlace.getPlace();
        PlaceResponseDto placeResponseDto = new PlaceResponseDto(
                place.getName(),
                place.getBasicAddress(),
                place.getDescription(),
                place.getLatitude(),
                place.getLongitude(),
                place.isUsed(),
                place.getImageUrl(),
                place.getUrl()
        );

        return new RecommendPlaceDto(
                recommendPlace.getDays(),
                recommendPlace.getSort(),
                placeResponseDto
        );
    }
    public RecommendPlaceResponseDto convertToResponseDto() {
        List<RecommendPlace> recommendPlaces = recommendPlaceRepository.findAll();
        List<RecommendPlaceDto> recommendPlaceDtos = recommendPlaces.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new RecommendPlaceResponseDto(recommendPlaceDtos);
    }

    public List<RecommendPlaceDto> convertToDtoList() {
        List<RecommendPlace> recommendPlaces = recommendPlaceRepository.findAll();
        return recommendPlaces.stream()
                .map(this::convertToDto)  // 🔹 오류 해결: this를 통해 현재 클래스의 메서드 호출
                .collect(Collectors.toList());
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
                .collect(Collectors.toMap(
                        AIResponseDto::getPlaceName, // 키: place_name
                        Function.identity(),        // 값: AIResponseDto 객체
                        (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
                ));

        // 4️⃣ AI 응답을 `RecommendPlaceDto`로 변환 (place_name을 기준으로 매칭)
        // 4️⃣ AI 응답을 `RecommendPlaceDto`로 변환 (place_name을 기준으로 매칭)
        List<RecommendPlaceDto> recommendPlaces = places.stream()
                .map(place -> {
                    AIResponseDto aiResponse = aiResponseMap.get(place.getName()); // ✅ O(1) 조회

                    // 🔹 AI 응답이 없는 경우 제외 (null 필터링)
                    if (aiResponse == null) {
                        return null;
                    }

                    return RecommendPlaceDto.builder()
                            .days(aiResponse.getDay())  // ✅ AI 응답에서 days 값 가져오기
                            .sort(aiResponse.getSort()) // ✅ AI 응답에서 sort 값 가져오기
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
                .filter(Objects::nonNull) // 🔹 AI 응답이 없는 (null) 원소 필터링
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

//this.day = day;
//        this.sort = sort;
//        this.placeName = placeName;
//        this.summary = summary;
    private List<AIResponseDto> getMockData() {
        return List.of(

                new AIResponseDto(1,1, "비비플로", "카페이면서 소품샵, 자체 제작 상품과 아카이빙 브랜드 상품 판매, 인스타그램 택배 주문 가능"),
                new AIResponseDto(1, 2, "이츠모라멘", "라멘 맛집"),
                new AIResponseDto(1, 3, "형제막국수", "막국수와 수육 세트 메뉴가 맛있는 곳, 비빔막국수 추천"),
                new AIResponseDto(2, 1, "여고시절 카레 떡볶이", "카레향이 나는 길거리 떡볶이집 느낌, 떡과 어묵 개수 선택 가능, 단맛보다 짠맛이 강함"),
                new AIResponseDto(2, 2, "강문해변", "주차장에서 해변으로 바로 연결되어 피크닉 하기 좋은 곳"),
                new AIResponseDto(2, 3, "슬로우슬로우담담", "일본 감성의 수제 도자기 소품 판매, 마음의 조각들 추천")

        );
    }


}
