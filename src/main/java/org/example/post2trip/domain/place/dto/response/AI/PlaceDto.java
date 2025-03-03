package org.example.post2trip.domain.place.dto.response.AI;

import jakarta.persistence.Column;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class PlaceDto {

    private Long id;

    @Column(name = "s_id", nullable = false)
    private String sid; //

    private String name;

    private String category;

    private String basicAddress;

    private String description;

    private String latitude;
    private String longitude;

    private boolean isUsed;

    private String url;

    private String imageUrl;
}
