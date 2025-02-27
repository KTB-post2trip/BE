package org.example.post2trip.domain.kakaoMap.application;

import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.kakaoMap.dto.image.KakaoImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakakoSearchService {

    private final RestTemplate restTemplate;

    @Value("${KAKAO.API.KEY}")
    private String apiKey;

    private final String baseUrl = "https://dapi.kakao.com/v2/search/image";

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

    public KakaoImageDto searchByKeyword(String query) {

        String url = baseUrl
                + "?query=" + query
                + "&size=5"
                + "&sort=accuracy";
        return callKakaoApi(url, KakaoImageDto.class);
    }
}
