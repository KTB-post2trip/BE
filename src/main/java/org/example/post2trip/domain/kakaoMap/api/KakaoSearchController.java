package org.example.post2trip.domain.kakaoMap.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.kakaoMap.application.KakakoSearchService;
import org.example.post2trip.domain.kakaoMap.dto.image.KakaoImageDto;
import org.example.post2trip.global.common.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakaoSearch")
@RequiredArgsConstructor
@Tag(name = "kakaoSearch", description = "카카오 검색 관련 api / 담당자 : 이영학")
public class KakaoSearchController {
    private final KakakoSearchService kakakoSearchService;

    @GetMapping("/search/address")
    public ResponseModel<?> search(
            @RequestParam(required = false) String query

    ) {
        KakaoImageDto kakaoImageDto  = kakakoSearchService.searchByKeyword(query);
        return ResponseModel.success(kakaoImageDto);
    }




}
