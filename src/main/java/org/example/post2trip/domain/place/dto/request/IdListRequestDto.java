package org.example.post2trip.domain.place.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class IdListRequestDto {
    private List<Long> ids;
}