package org.example.post2trip.domain.place.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class PlaceRequestDto {
    private String name;

    private String basicAddress;

    private String description;

    private String latitude;
    private String longitude;

    private boolean isUsed;

    private String imageUrl;
}
