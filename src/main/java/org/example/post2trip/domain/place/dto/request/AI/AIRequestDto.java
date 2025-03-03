package org.example.post2trip.domain.place.dto.request.AI;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class AIRequestDto {
    private int days;
    private List<AIPlaceDto> places;
}
