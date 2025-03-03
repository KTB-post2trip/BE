package org.example.post2trip.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.domain.RecommendPlace;

@Getter
@Setter
@Builder
public class RecommendPlaceDto {
    private int days;
    private int sort;
    private PlaceResponseDto place;



    // 생성자
    public RecommendPlaceDto(int days, int sort, PlaceResponseDto place) {
        this.days = days;
        this.sort = sort;
        this.place = place;
    }

}