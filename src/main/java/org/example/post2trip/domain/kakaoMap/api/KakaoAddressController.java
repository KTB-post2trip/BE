package org.example.post2trip.domain.kakaoMap.api;

import org.example.post2trip.domain.kakaoMap.application.KakaoAddressSearchService;
import org.example.post2trip.domain.kakaoMap.dto.KakaoApiResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.KakaoKeywordResponseDto;
import org.example.post2trip.domain.kakaoMap.dto.KakaoTransCoordResponseDto;
import org.example.post2trip.domain.place.dto.response.PlaceReponseDto;
import org.example.post2trip.global.common.ResponseModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kakaoAddress")
@RequiredArgsConstructor
@Tag(name = "KakaoAddress", description = "카카오 지도 관련 api / 담당자 : 이영학")
public class KakaoAddressController {
    private final KakaoAddressSearchService kakaoAddressSearchService;

    @GetMapping("/search/address")
    public ResponseModel<?> searchAddress(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.searchAddress(query, page, size);
        return ResponseModel.success(kakaoApiResponseDto);
    }

    @GetMapping("/geo/coord2regioncode")
    public ResponseModel<?> getRegionCode(
            @RequestParam double x,
            @RequestParam double y
    ) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.coordToRegionCode(x, y);
        return ResponseModel.success(kakaoApiResponseDto);
    }

    @GetMapping("/geo/coord2address")
    public ResponseModel<?> getAddress(
            @RequestParam double x,
            @RequestParam double y
    ) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.coordToAddress(x, y);
        return ResponseModel.success(kakaoApiResponseDto);
    }

    @GetMapping("/search/keyword")
    public ResponseModel<?> searchByKeyword(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "-9999.0") double x,
            @RequestParam(defaultValue = "-9999.0") double y,
            @RequestParam(defaultValue = "20000") int radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        KakaoKeywordResponseDto kakaoKeywordResponseDto = kakaoAddressSearchService.searchByKeywordWithRadius(query, x,
                y, radius, page, size);
        return ResponseModel.success(kakaoKeywordResponseDto);
    }

    @GetMapping("/geo/transcoord")
    public ResponseModel<?> transCoord(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam(defaultValue = "WGS84") String inputCoord,
            @RequestParam(defaultValue = "WGS84") String outputCoord
    ) {
        KakaoTransCoordResponseDto kakaoTransCoordResponseDto = kakaoAddressSearchService.transCoord(x, y, inputCoord,
                outputCoord);
        return ResponseModel.success(kakaoTransCoordResponseDto);
    }


    @GetMapping("/search/place")
    public ResponseModel<?> searchPlace(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "-9999.0") double x,
            @RequestParam(defaultValue = "-9999.0") double y,
            @RequestParam(defaultValue = "20000") int radius
    ) {
        PlaceReponseDto dto = kakaoAddressSearchService.searchByKeyword(query, x, y, radius);
        return ResponseModel.success(dto);
    }
}