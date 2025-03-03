package org.example.post2trip.global.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.request.PlaceDto;

import java.util.List;

public class JsonParser {
    public static List<Place> parseJsonToPlaces(String jsonBody, String sid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON을 List<PlaceDto>로 변환
            List<PlaceDto> placeDtoList = objectMapper.readValue(jsonBody, new TypeReference<>() {});

            // PlaceDto → Place 변환
            return placeDtoList.stream()
                    .map(dto -> Place.builder()
                            .sid(sid)  // sid 설정
                            .name(dto.getName())
                            .category(dto.getCategory())
                            .basicAddress("")  // 🔹 기본 주소는 Kakao API에서 조회 필요
                            .description(dto.getDescription())
                            .latitude("") // 🔹 좌표도 Kakao API에서 조회 필요
                            .longitude("")
                            .isUsed(false)
                            .imageUrl("")
                            .url("")
                            .build())
                    .toList();

        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}
