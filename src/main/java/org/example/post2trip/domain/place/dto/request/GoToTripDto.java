package org.example.post2trip.domain.place.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class GoToTripDto {
    String url;
    String placeName;
}
