package org.example.post2trip.domain.place.application;

import lombok.RequiredArgsConstructor;
import org.example.post2trip.domain.place.dao.PlaceRepository;
import org.example.post2trip.domain.place.domain.Place;
import org.example.post2trip.domain.place.dto.response.PlaceReponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {
    private final PlaceRepository placeRepository;



    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id);
    }

    public void createPlace(PlaceReponseDto place) {
        Place newPlace = Place.builder()
                .name(place.getName())
                .description(place.getDescription())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .basicAddress(place.getBasicAddress())
                .build();
        placeRepository.save(newPlace);
    }

    @Transactional
    public List<Place> updatePlaces(List<Long> ids) {
        // 1. ID 리스트에 해당하는 모든 Place 엔티티 조회
        List<Place> places = placeRepository.findAllById(ids);

        // 2. 조회된 Place 리스트의 isUsed 값을 true로 변경
        places.forEach(place -> place.setUsed(true));

        // 3. 변경된 Place 리스트 저장 후 반환
        return placeRepository.saveAll(places);
    }

    public Place updatePlace(Long id, Place newPlaceData) {
        return placeRepository.findById(id)
                .map(existingPlace -> {
                    // 기존 객체를 기반으로 새로운 객체 생성 (Builder 사용)
                    Place updatedPlace = existingPlace.builder()
                            .name(newPlaceData.getName())
                            .basicAddress(newPlaceData.getBasicAddress())
                            .description(newPlaceData.getDescription())
                            .latitude(newPlaceData.getLatitude())
                            .longitude(newPlaceData.getLongitude())
                            .isUsed(newPlaceData.isUsed())
                            .imageUrl(newPlaceData.getImageUrl())
                            .build();

                    return placeRepository.save(updatedPlace);
                }).orElseThrow(() -> new RuntimeException("Place not found with id: " + id));
    }



    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }

}
