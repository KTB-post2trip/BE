package org.example.post2trip.domain.kakaoMap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("region_1depth_name")
    private String region1DepthName;

    @JsonProperty("region_2depth_name")
    private String region2DepthName;

    @JsonProperty("region_3depth_name")
    private String region3DepthName;

    @JsonProperty("mountain_yn")
    private String mountainYn;

    @JsonProperty("main_address_no")
    private String mainAddressNo;

    @JsonProperty("sub_address_no")
    private String subAddressNo;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("x")
    private String x;

    @JsonProperty("y")
    private String y;
}