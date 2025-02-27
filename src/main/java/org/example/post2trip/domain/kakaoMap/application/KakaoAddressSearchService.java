package org.example.post2trip.domain.kakaoMap.application;

import org.example.post2trip.domain.kakaoMap.dto.map.KakaoApiResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.map.KakaoKeywordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.map.KakaoTransCoordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.image.KakaoImageDto;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.response.PlaceResponseDto;
import org.example.post2trip.domain.place.dto.response.ProcessUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;
    private final KakakoSearchService kakaoSearchService;

    @Value("${KAKAO.API.KEY}")
    private String apiKey;

    private final String baseUrl = "https://dapi.kakao.com/v2/local";

    private <T> T callKakaoApi(String url, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                responseType
        );
        System.out.println(url);
        return response.getBody();
    }

    public KakaoApiResponseDto searchAddress(String query, int page, int size) {
        //String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = baseUrl
                + "/search/address.json"
                + "?query=" + query
                + "&page=" + page
                + "&size=" + size
                + "&analyze_type=similar";

        return callKakaoApi(url, KakaoApiResponseDto.class);
    }


    public KakaoApiResponseDto coordToRegionCode(double x, double y) {
        String url = baseUrl
                + "/geo/coord2regioncode.json"
                + "?x=" + x
                + "&y=" + y;

        return callKakaoApi(url, KakaoApiResponseDto.class);
    }

    public KakaoApiResponseDto coordToAddress(double x, double y) {
        String url = baseUrl
                + "/geo/coord2address.json"
                + "?x=" + x
                + "&y=" + y;

        return callKakaoApi(url, KakaoApiResponseDto.class);
    }

    public KakaoKeywordResponseDto searchByKeywordWithRadius(String query, double x, double y, int radius, int page, int size) {
        String location = (x != -9999) ? "&x=" + x + "&y=" + y + "&radius=" + radius : "";

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + query
                + location
                + "&page=" + page
                + "&size=" + size
             //   + "&sort=accuracy"
                + "&analyze_type=similar";

        // 1차 검색 실행
        KakaoKeywordResponseDto response = callKakaoApi(url, KakaoKeywordResponseDto.class);
        System.out.println(response.getMeta().getSameName().getKeyword());
        // 결과가 없을 경우 자동 교정 키워드 확인
        if (response.getDocuments().isEmpty() && response.getMeta().getSameName() != null) {
            String correctedKeyword = response.getMeta().getSameName().getKeyword();

            if (correctedKeyword != null && !correctedKeyword.isEmpty() && !correctedKeyword.equals(query)) {


                // 교정된 키워드로 재검색 실행
                String correctedUrl = baseUrl
                        + "/search/keyword.json"
                        + "?query=" + correctedKeyword
                        + location
                        + "&page=" + page
                        + "&size=" + 1
                        + "&analyze_type=similar";

                return callKakaoApi(correctedUrl, KakaoKeywordResponseDto.class);
            }
        }

        return response;
    }


    public KakaoTransCoordResponseDto transCoord(double x, double y, String inputCoord, String outputCoord) {
        String url = baseUrl
                + "/geo/transcoord.json"
                + "?x=" + x
                + "&y=" + y
                + "&input_coord=" + inputCoord
                + "&output_coord=" + outputCoord;

        return callKakaoApi(url, KakaoTransCoordResponseDto.class);
    }


    public PlaceResponseDto searchByKeyword(String query, double x, double y, int radius) {
        String location = (x != -9999) ? "&x=" + x + "&y=" + y + "&radius=" + radius : "";

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + query
                + location
                + "&page=" + 1
              //  + "&sort=accuracy"
                + "&size=" + 1
                + "&analyze_type=similar";

        // 1차 검색 실행
        KakaoKeywordResponseDto response = callKakaoApi(url, KakaoKeywordResponseDto.class);

        // 🔹 검색 결과가 없을 경우 빈 객체 반환
        if (response == null || response.getDocuments().isEmpty()) {
            return PlaceResponseDto.builder()
                    .name("")
                    .basicAddress("")
                    .latitude("")
                    .longitude("")
                    .isUsed(false)
                    .imageUrl("")
                    .build();
        }

        // 🔹 이미지 검색 수행
        KakaoImageDto image = kakaoSearchService.searchByKeyword(response.getDocuments().get(0).getPlaceName());

        String imageUrl = image.getDocuments().stream()
                .map(doc -> doc.getImageUrl())  // 🔹 KakaoImageDto의 문서에서 `getImageUrl()` 추출
                .filter(imgUrl -> imgUrl != null && !imgUrl.contains("postfiles")) // 🔹 "postfiles"가 포함된 이미지 제외
                .findFirst() // 🔹 첫 번째 적절한 이미지 찾기
                .orElse(""); // 🔹 없다면 빈 문자열 반환




        // 🔹 검색 결과 반환
        return PlaceResponseDto.builder()
                .name(response.getDocuments().get(0).getPlaceName())
                .basicAddress(response.getDocuments().get(0).getAddressName())
                .latitude(response.getDocuments().get(0).getY())
                .longitude(response.getDocuments().get(0).getX())
                .isUsed(false)
                .imageUrl(imageUrl)
                .url(response.getDocuments().get(0).getPlaceUrl())
                .build();
    }


    // 🔹 AI 서버 응답을 기반으로 Place 객체 생성

    public Place searchByKeywords(double x, double y, int radius, String sid, ProcessUrlResponseDto dto,String placeName) {
        String location = (x != -9999) ? "&x=" + x + "&y=" + y + "&radius=" + radius : "";

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + dto.getPlace_name()
                + location
                + "&page=" + 1
                + "&size=" + 1
                + "&analyze_type=similar";

        // 1차 검색 실행
        KakaoKeywordResponseDto response = callKakaoApi(url, KakaoKeywordResponseDto.class);

        // 🔹 검색 결과가 없을 경우 빈 객체 반환
        if (response == null || response.getDocuments().isEmpty() || !response.getDocuments().get(0).getAddressName().contains(placeName)){
            return Place.builder()
                    .name("")
                    .basicAddress("")
                    .latitude("")
                    .longitude("")
                    .isUsed(false)
                    .imageUrl("")
                    .build();
        }

        // 🔹 이미지 검색 수행
        KakaoImageDto image = kakaoSearchService.searchByKeyword(response.getDocuments().get(0).getPlaceName());
        // 🔹 3. 이미지 URL 가져오기 (postfiles 포함 제거 + 긴 URL 체크)
        // 🔹 3. 이미지 URL 가져오기 (postfiles 제외 + 길이 제한)
        String imageUrl = image.getDocuments().stream()
                .map(KakaoImageDto.DocumentDto::getImageUrl) // ✅ `DocumentDto`에서 `imageUrl` 가져오기
                .filter(Objects::nonNull) // ✅ Null 값 제거
                .filter(imgUrl -> !imgUrl.contains("postfiles")) // ✅ "postfiles"가 포함된 이미지 제외
                .filter(imgUrl -> imgUrl.length() <= 255) // ✅ 너무 긴 URL 제거
                .findFirst()
                .orElse(""); // ✅ 없으면 빈 값 반환


        // 🔹 검색 결과 반환
        return Place.builder()
                .sid(sid+"")
                .name(dto.getPlace_name())
                .category(dto.getCategory())
                .basicAddress(response.getDocuments().get(0).getAddressName())
                .description(dto.getSummary())
                .latitude(response.getDocuments().get(0).getY())
                .longitude(response.getDocuments().get(0).getX())
                .isUsed(false)
                .imageUrl(imageUrl)
                .url(response.getDocuments().get(0).getPlaceUrl())
                .build();
    }



}