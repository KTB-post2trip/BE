package org.example.post2trip.domain.place.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import java.io.IOException;




@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "s_id", nullable = false, length = 50)  // 🔹 String으로 변경
    private String sid;


    private String name;

    private String category;

    private String basicAddress;

    private String description;

    private String latitude;
    private String longitude;

    private boolean isUsed;

    private String url;

    private String imageUrl;



    public Place() {
    }

    // ✅ Getter에서 Long으로 변환하여 반환
    public Long getSidAsLong() {
        try {
            return Long.parseLong(sid);
        } catch (NumberFormatException e) {
            return null; // 변환 실패 시 null 반환 (예외 방지)
        }
    }

    // ✅ Getter에서 Long으로 변환하여 반환

}
