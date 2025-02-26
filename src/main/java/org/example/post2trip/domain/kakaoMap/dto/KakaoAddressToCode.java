package org.example.post2trip.domain.kakaoMap.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class KakaoAddressToCode {
    @JsonProperty("meta")
    private MetaDto meta;

    @JsonProperty("documents")
    private List<DocumentDto> documents;

    @Getter
    public static class MetaDto {
        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("is_end")
        private boolean isEnd;
    }

    @Getter
    public static class DocumentDto {
        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;
    }

}