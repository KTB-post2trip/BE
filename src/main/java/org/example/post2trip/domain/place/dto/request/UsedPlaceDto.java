package org.example.post2trip.domain.place.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;


@Data
@Getter
@Builder
public class UsedPlaceDto {

    private List<PlaceDto> places;
    private Integer days;

}
