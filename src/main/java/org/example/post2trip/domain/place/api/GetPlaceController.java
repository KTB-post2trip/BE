package org.example.post2trip.domain.place.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.kakaoMap.dto.KakaoApiResponseDto;
import org.example.post2trip.domain.place.application.AIService;
import org.example.post2trip.global.common.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@Tag(name = "place", description = "장소 가져오기 api / 담당자 : 이영학")
public class GetPlaceController {

    private final AIService aiService;

  /*  @GetMapping("")
    public CompletableFuture<ResponseModel<?>> getPlace(@RequestParam(required = false) String url) {
        return aiService.getBusinessName(url)
                .thenApply(businessName -> ResponseModel.success(businessName)) // 성공 시
                .exceptionally(); // 실패 시
    }*/
}
