package org.example.post2trip.domain.place.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.post2trip.domain.place.dto.request.RecommendPlaceRequestDto;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor // ✅ 기본 생성자 추가
@AllArgsConstructor // ✅ 모든 필드를 포함한 public 생성자 추가
public class RecommendPlaceResponseDto {
    @JsonProperty("recommend_places")
    private List<RecommendPlaceDto> places;
}