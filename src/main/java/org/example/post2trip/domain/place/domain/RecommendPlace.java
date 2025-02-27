package org.example.post2trip.domain.place.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recommend_place")
public class RecommendPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int days;
    private int sort;

    @OneToOne
    @JoinColumn(name = "place_id")
    private Place place;


}
