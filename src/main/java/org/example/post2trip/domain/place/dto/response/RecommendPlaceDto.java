package org.example.post2trip.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecommendPlaceDto {
    private int days;
    private int sort;
    private PlaceResponseDto place;
}