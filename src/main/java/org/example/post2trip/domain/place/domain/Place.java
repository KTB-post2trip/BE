package org.example.post2trip.domain.place.domain;

import jakarta.persistence.*;
import lombok.*;


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

    @Column(name = "s_id")
    private String sId;

    private String name;

    private String category;

    private String basicAddress;

    private String description;

    private String latitude;
    private String longitude;

    private boolean isUsed;

    private String imageUrl;



    public Place() {
    }

}
