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

    public PlaceResponseDto(String name, String basicAddress, String description,
                            String latitude, String longitude, boolean isUsed,
                            String imageUrl, String url) {
        this.name = name;
        this.basicAddress = basicAddress;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isUsed = isUsed;
        this.imageUrl = imageUrl;
        this.url = url;
    }

}
