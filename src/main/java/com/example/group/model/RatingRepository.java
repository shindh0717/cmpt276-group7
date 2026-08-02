package com.example.group.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByLatitudeAndLongitude(Double latitude, Double longitude);
    Optional<Rating> findByUserIdAndLatitudeAndLongitude(Long userId, Double latitude, Double longitude);
}


