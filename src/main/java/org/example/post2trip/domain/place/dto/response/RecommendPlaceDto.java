package org.example.post2trip.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.post2trip.domain.place.dto.request.PlaceDto;

@Getter
@Setter
@Builder
public class RecommendPlaceDto {
    private int days;
    private int sort;
    private PlaceReponseDto place;
}