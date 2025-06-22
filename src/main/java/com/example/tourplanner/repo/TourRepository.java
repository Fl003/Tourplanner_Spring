package com.example.tourplanner.repo;

import com.example.tourplanner.model.Tour;
import org.springframework.data.repository.CrudRepository;

// Data Access Layer

public interface TourRepository extends CrudRepository<Tour, Long> {

    Integer removeTourById(Long id);

    Tour findTopByOrderByIdDesc();
}
