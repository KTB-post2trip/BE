package org.example.post2trip.domain.place.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@Builder
public class RecommendPlaceRequestDto {

    private int days;
    private List<Long> ids;
}
