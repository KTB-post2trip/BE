package org.example.post2trip.domain.place.dto.request.AI;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIPlaceDto {
    private Long id;
    private String category;
    private String place_name;
    private String summary;
    private double latitude;
    private double longitude;
}
