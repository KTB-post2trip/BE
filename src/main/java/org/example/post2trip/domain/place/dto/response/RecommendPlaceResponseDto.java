package org.example.post2trip.domain.place.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.post2trip.domain.place.dto.request.RecommendPlaceRequestDto;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor // ✅ 기본 생성자 추가
public class RecommendPlaceResponseDto {
    @JsonProperty("recommend_places")
    private List<RecommendPlaceDto> places;


    // 생성자
    public RecommendPlaceResponseDto(List<RecommendPlaceDto> places) {
        this.places = places;
    }
}