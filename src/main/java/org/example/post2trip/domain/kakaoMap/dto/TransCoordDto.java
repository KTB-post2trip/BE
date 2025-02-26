package org.example.post2trip.domain.kakaoMap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransCoordDto {
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;
}