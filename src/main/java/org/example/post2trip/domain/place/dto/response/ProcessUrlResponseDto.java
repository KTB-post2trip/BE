package org.example.post2trip.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProcessUrlResponseDto {
    private int id;
    private String category;
    private String place_name;
    private String summary;

    public ProcessUrlResponseDto(int id, String category, String place_name, String summary) {
        this.id = id;
        this.category = category;
        this.place_name = place_name;
        this.summary = summary;
    }
}