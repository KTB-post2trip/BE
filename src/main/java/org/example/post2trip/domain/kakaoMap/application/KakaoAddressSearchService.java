package org.example.post2trip.domain.kakaoMap.application;

import org.example.post2trip.domain.kakaoMap.dto.map.KakaoAddressToCode;
import org.example.post2trip.domain.kakaoMap.dto.map.KakaoApiResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.map.KakaoKeywordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.map.KakaoTransCoordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.image.KakaoImageDto;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.response.PlaceReponseDto;
import org.example.post2trip.domain.place.dto.response.ProcessUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import lombok.RequiredArgsConstructor;

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

    public KakaoAddressToCode searchAddressXY(String query, int page, int size) {
        //String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        {
            String location = "";

            String url = baseUrl
                    + "/search/keyword.json"
                    + "?query=" + query
                    + location
                    + "&page=" + page
                    + "&size=" + size
                    + "&analyze_type=similar";
            return callKakaoApi(url, KakaoAddressToCode.class);
        }
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

    public KakaoKeywordResponseDto searchByKeyword(String query, int page, int size) {

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + query
                + "&page=" + page
                + "&size=" + size
                + "&analyze_type=similar";
        return callKakaoApi(url, KakaoKeywordResponseDto.class);
    }

    public KakaoKeywordResponseDto searchByCategory(String categoryGroupCode, double x, double y, int radius, int page,
                                                    int size) {
        String url = baseUrl
                + "/search/category.json"
                + "?category_group_code=" + categoryGroupCode
                + "&x=" + x
                + "&y=" + y
                + "&radius=" + radius
                + "&page=" + page
                + "&size=" + size;

        return callKakaoApi(url, KakaoKeywordResponseDto.class);
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


    public PlaceReponseDto searchByKeyword(String query, double x, double y, int radius) {
        String location = (x != -9999) ? "&x=" + x + "&y=" + y + "&radius=" + radius : "";

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + query
                + location
                + "&page=" + 1
                + "&size=" + 1
                + "&analyze_type=similar";

        // 1차 검색 실행
        KakaoKeywordResponseDto response = callKakaoApi(url, KakaoKeywordResponseDto.class);

        // 🔹 검색 결과가 없을 경우 빈 객체 반환
        if (response == null || response.getDocuments().isEmpty()) {
            return PlaceReponseDto.builder()
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
        String imageUrl = (image.getDocuments().isEmpty()) ? "" : image.getDocuments().get(0).getImageUrl();

        // 🔹 검색 결과 반환
        return PlaceReponseDto.builder()
                .name(response.getDocuments().get(0).getPlaceName())
                .basicAddress(response.getDocuments().get(0).getAddressName())
                .latitude(response.getDocuments().get(0).getY())
                .longitude(response.getDocuments().get(0).getX())
                .isUsed(false)
                .imageUrl(imageUrl)
                .url(response.getDocuments().get(0).getPlaceUrl())
                .build();
    }



    public Place searchByKeywords(double x, double y, int radius, Long sid, ProcessUrlResponseDto dto) {
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
        if (response == null || response.getDocuments().isEmpty()) {
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
        String imageUrl = (image.getDocuments().isEmpty()) ? "" : image.getDocuments().get(0).getImageUrl();

        // 🔹 검색 결과 반환
        return Place.builder()
                .sid(sid)
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