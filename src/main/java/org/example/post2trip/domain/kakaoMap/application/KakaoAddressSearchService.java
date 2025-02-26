package org.example.post2trip.domain.kakaoMap.application;

import org.example.post2trip.domain.kakaoMap.dto.KakaoAddressToCode;
import org.example.post2trip.domain.kakaoMap.dto.KakaoApiResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.KakaoKeywordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.KakaoTransCoordResponseDto;
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

    public KakaoKeywordResponseDto searchByKeywordWithRadius(String query, double x, double y, int radius, int page,
                                                             int size) {
        String location;
        if (x != -9999) {
            location = ""
                    + "&x=" + x
                    + "&y=" + y
                    + "&radius=" + radius;
        } else {
            location = "";
        }

        String url = baseUrl
                + "/search/keyword.json"
                + "?query=" + query
                + location
                + "&page=" + page
                + "&size=" + size
                + "&analyze_type=similar";
        return callKakaoApi(url, KakaoKeywordResponseDto.class);
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
}