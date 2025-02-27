package org.example.post2trip.domain.place.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.example.post2trip.domain.kakaoMap.application.KakaoAddressSearchService;
import org.example.post2trip.domain.place.dao.PlaceRepository;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.request.AI.AIRequestDto;
import org.example.post2trip.domain.place.dto.request.ProcessUrlRequestDto;
import org.example.post2trip.domain.place.dto.response.AI.AIResponseDto;
import org.example.post2trip.domain.place.dto.response.ProcessUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;
    private final KakaoAddressSearchService kakaoAddressSearchService;

    private final PlaceRepository placeRepository;

    @Value("${ai.server.url}")
    private String aiServerUrl; // AI 서버 URL 환경 변수에서 로드

    // 🔹 특정 장소명에 따른 좌표 매핑
    private static final Map<String, double[]> PLACE_COORDINATES = Map.of(
            "서울", new double[]{37.566826004661, 126.978652258309},
            "경기", new double[]{37.2749769872425, 127.00892996953},
            "제주", new double[]{33.4889179032603, 126.498229141199},
            "부산", new double[]{35.1798200522868, 129.075087492149},
            "강원", new double[]{37.8853257858209, 127.729829010354},
            "전남", new double[]{34.8160821478848, 126.462788333376},
            "경상", new double[]{36.5759962255808, 128.505799255401},
            "강릉", new double[]{37.7948207421998, 128.919175274112}, // 기존 데이터 유지
            "도쿄 타워 호텔", new double[]{35.66, 139.75}, // 기존 데이터 유지
            "기본값", new double[]{-9999.0, -9999.0} // 기본값 처리
    );



    @Async
    public CompletableFuture<List<Place>> processUrlAsync(String url, String placeName) {
        // AI 서버 엔드포인트
        List<ProcessUrlResponseDto> responseList;

        try {
            System.out.println("AI 서버로 요청 보내기: " + aiServerUrl + "/process-url?url=" + url);

            // 🔹 AI 서버로 GET 요청 보내기 (쿼리 파라미터 사용)
            ResponseEntity<ProcessUrlResponseDto[]> responseEntity = restTemplate.exchange(
                    aiServerUrl + "/process-url?url=" + url,
                    HttpMethod.GET,
                    null,
                    ProcessUrlResponseDto[].class
            );

            // 응답이 null이면 빈 리스트 반환
            responseList = (responseEntity.getBody() != null) ?
                    Arrays.asList(responseEntity.getBody()) : List.of();
        } catch (Exception e) {
            responseList = List.of(); // AI 서버 오류 시 빈 리스트 반환
        }

        // ✅ AI 서버 응답이 없으면 테스트 데이터 삽입
        if (responseList.isEmpty()) {
            responseList = getMockData();
        }

        // 🔹 placeName에 따른 x, y 값 적용
        double[] coordinates = PLACE_COORDINATES.getOrDefault(placeName, PLACE_COORDINATES.get("기본값"));
        double x = coordinates[0];
        double y = coordinates[1];

        // 🔹 실행할 때마다 고유한 `sid` 생성
        Long sid = generateUniqueSid();

        // 🔹 `sid`가 이미 존재하는지 확인
        boolean exists = placeRepository.existsBySid(sid);
        if (exists) {
            List<Place> existingPlaces = placeRepository.findBySid(sid);
            return CompletableFuture.completedFuture(existingPlaces);
        }

        // 🔹 응답 리스트를 기반으로 Place 리스트 생성
        List<Place> placeList = responseList.stream()
                .map(dto -> kakaoAddressSearchService.searchByKeywords(x, y, 20000, sid, dto))
                .collect(Collectors.toList());

        // 🔹 Place 리스트를 한꺼번에 저장
        List<Place> savedPlaces = placeRepository.saveAll(placeList);

        // 🔹 저장된 Place 리스트 반환
        return CompletableFuture.completedFuture(savedPlaces);
    }

    // 🔹 UUID 기반 고유한 Long 타입 ID 생성
    private Long generateUniqueSid() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }


    // ✅ Mock 데이터 제공 메서드
    private List<ProcessUrlResponseDto> getMockData() {
        return List.of(
                new ProcessUrlResponseDto(4, "카페/디저트", "카페삐삐", "모던하고 컬러풀한 감성의 카페, 브런치 메뉴(샌드위치, 스프) 판매"),
                new ProcessUrlResponseDto(5, "카페/디저트", "비비플로", "카페이면서 소품샵, 자체 제작 상품과 아카이빙 브랜드 상품 판매, 인스타그램 택배 주문 가능"),
                new ProcessUrlResponseDto(6, "음식점", "이츠모라멘", "라멘 맛집"),
                new ProcessUrlResponseDto(7, "음식점", "형제막국수", "막국수와 수육 세트 메뉴가 맛있는 곳, 비빔막국수 추천"),
                new ProcessUrlResponseDto(8, "음식점", "여고시절 카레 떡볶이", "카레향이 나는 길거리 떡볶이집 느낌, 떡과 어묵 개수 선택 가능, 단맛보다 짠맛이 강함"),
                new ProcessUrlResponseDto(9, "관광지", "강문해변", "주차장에서 해변으로 바로 연결되어 피크닉 하기 좋은 곳"),
                new ProcessUrlResponseDto(10, "쇼핑", "슬로우슬로우담담", "일본 감성의 수제 도자기 소품 판매, 마음의 조각들 추천"),
                new ProcessUrlResponseDto(11, "쇼핑", "르봉마젤", "프랑스 파리 감성의 식기와 소품 판매, 1, 2층은 소품샵, 탐사층은 카페"),
                new ProcessUrlResponseDto(14, "기타", "히든서프", "서핑 강습, 숙박, 파티 가능, 강릉역에서 픽업 가능")
        );
    }
    private final ObjectMapper objectMapper; // JSON 변환 객체

    public List<AIResponseDto> sendRequestToAIServer(AIRequestDto aiRequest) {
        String fullUrl = aiServerUrl + "/api/recommend";
        System.out.println("🔹 AI 서버로 요청 보내기 (POST): " + fullUrl);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ AIRequestDto 객체를 JSON 문자열로 변환하여 출력
            String jsonRequest = objectMapper.writeValueAsString(aiRequest);
            System.out.println("🔹 요청 데이터 (JSON): " + jsonRequest);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

            // 🔹 AI 서버로 요청 보내기
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    fullUrl, HttpMethod.POST, requestEntity, String.class
            );

            // ✅ JSON 응답 출력
            String jsonResponse = responseEntity.getBody();
            System.out.println("🔹 AI 서버 응답 (JSON): " + jsonResponse);

            // 🔹 JSON 문자열을 AIResponseDto 리스트로 변환하여 반환
            return objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, AIResponseDto.class));
        } catch (Exception e) {
            System.err.println("❌ AI 서버 요청 실패! 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // 🔹 오류 발생 시 빈 리스트 반환
        }
    }
}

