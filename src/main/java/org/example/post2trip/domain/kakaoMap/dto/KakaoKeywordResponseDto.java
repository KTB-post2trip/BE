package org.example.post2trip.domain.kakaoMap.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoKeywordResponseDto {
    @JsonProperty("meta")
    private MetaDto meta;

    @JsonProperty("documents")
    private List<KeywordSearchDto> documents;

    @Getter
    @Setter
    public static class MetaDto {
        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("same_name")
        private SameNameDto sameName;

        @Getter
        @Setter
        public static class SameNameDto {
            @JsonProperty("region")
            private List<String> region;

            @JsonProperty("keyword")
            private String keyword;

            @JsonProperty("selected_region")
            private String selectedRegion;
        }
    }
}