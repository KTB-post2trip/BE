package org.example.post2trip.domain.kakaoMap.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoImageDto {

    @JsonProperty("meta")
    private MetaDto meta;

    @JsonProperty("documents")
    private List<DocumentDto> documents;

    @Getter
    @Setter
    public static class MetaDto {
        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("is_end")
        private boolean isEnd;
    }
    @Getter
    @Setter
    public static class DocumentDto {


        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("width")
        private int width;

        @JsonProperty("height")
        private int height;

    }

}
