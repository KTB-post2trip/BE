package org.example.post2trip.domain.place.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class PlaceDto {
    @JsonProperty("place_name")
    private String name;
    private String description;
    private String latitude;
    private String longitude;
    private String category;
    private String basicAddress;
}
