package org.example.post2trip.domain.kakaoMap.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageDto {

    @JsonProperty("meta")
    private KakaoImageDto.MetaDto meta;

    @JsonProperty("documents")
    private List<KakaoImageDto.DocumentDto> documents;
}
