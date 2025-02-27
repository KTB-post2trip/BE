package org.example.post2trip.domain.place.dto.response.AI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIResponseDto {
    @JsonProperty("day")  // JSON 필드명과 매핑
    private int day;

    @JsonProperty("sort")
    private int sort;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("summary")
    private String summary;

    public AIResponseDto(int day, int sort, String placeName, String summary) {
        this.day = day;
        this.sort = sort;
        this.placeName = placeName;
        this.summary = summary;
    }
}
