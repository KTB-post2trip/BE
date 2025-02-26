package org.example.post2trip.domain.kakaoMap.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTransCoordResponseDto {
    @JsonProperty("meta")
    private MetaDto meta;

    @JsonProperty("documents")
    private List<TransCoordDto> documents;

    @Getter
    @Setter
    public static class MetaDto {
        @JsonProperty("total_count")
        private int totalCount;
    }
}