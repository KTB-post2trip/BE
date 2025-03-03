package org.example.post2trip.domain.place.dao;

import org.example.post2trip.domain.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findBySid(String sid); // 🔹 메서드명도 필드명과 일치하도록 변경
    List<Place> findBySidAndIsUsed(String sid, boolean isUsed); // 🔹 메서드명도 필드명과 일치하도록 변경

    boolean existsBySid(String sid);
}