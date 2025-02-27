package org.example.post2trip.domain.place.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class PlaceResponseDto {
    @JsonProperty("place_name")
    private String name;

    private String basicAddress;

    private String description;

    private String latitude;
    private String longitude;

    private boolean isUsed;

    private String imageUrl;
    private String url;



}
