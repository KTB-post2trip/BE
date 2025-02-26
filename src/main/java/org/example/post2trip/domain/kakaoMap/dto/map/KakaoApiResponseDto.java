package org.example.post2trip.domain.kakaoMap.dto.map;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoApiResponseDto {
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

        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        @JsonProperty("region_2depth_name")
        private String region2DepthName;

        @JsonProperty("region_3depth_name")
        private String region3DepthName;

        @JsonProperty("region_4depth_name")
        private String region4DepthName;

        private String code;

        @JsonProperty("x")
        private String x;

        @JsonProperty("y")
        private String y;

        @JsonProperty("address_type")
        private String addressType;

        @JsonProperty("address")
        private AddressDto address;

        @JsonProperty("road_address")
        private RoadAddressDto roadAddress;
    }
}