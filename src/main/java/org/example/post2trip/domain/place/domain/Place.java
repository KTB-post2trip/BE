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

    @Column(name = "s_id")  // 🔹 String으로 변경
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


    // 생성자 (DTO -> 엔티티 변환용)
    public Place(String name, String basicAddress, String description, String latitude,
                 String longitude, boolean isUsed, String imageUrl, String url) {
        this.name = name;
        this.basicAddress = basicAddress;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isUsed = isUsed;
        this.imageUrl = imageUrl;
        this.url = url;
    }


    public Place() {
    }



}
