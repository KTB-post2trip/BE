package org.example.post2trip.domain.place.application;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl; // AI 서버 URL 환경 변수에서 로드



    /*@Async
    public CompletableFuture<commonPlaceListDto> getBusinessName(String youtubeUrl) {
        try {
            // AI 서버에 요청을 보내어 유튜브 URL을 분석하여 상호명 반환
            String requestUrl = aiServerUrl + "?url=" + URLEncoder.encode(youtubeUrl, StandardCharsets.UTF_8);

            ResponseEntity<commonPlaceListDto> response = restTemplate.getForEntity(requestUrl, commonPlaceListDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return CompletableFuture.completedFuture(response.getBody());
            } else {
                return CompletableFuture.failedFuture(new RuntimeException("AI 서버 응답 실패: " + response.getStatusCode()));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e); // 예외 발생 시 CompletableFuture를 실패 상태로 설정
        }
    }*/



}
