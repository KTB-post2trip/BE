package org.example.post2trip.domain.place.dao;

import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.domain.RecommendPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendPlaceRepository extends JpaRepository<RecommendPlace, Long> {

    Optional<RecommendPlace> findByPlace(Place place);
}