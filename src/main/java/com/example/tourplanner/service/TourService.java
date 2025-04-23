package com.example.tourplanner.service;

import com.example.tourplanner.model.Tour;
import com.example.tourplanner.repo.TourRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

// Business Logic Layer

@Named
public class TourService {
    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public Tour getTour(Long id) {
        return this.tourRepository.findById(id).orElse(null);
    }

    public Tour createTour(Tour tour) {
        return this.tourRepository.save(tour);
    }

    public Iterable<Tour> getTours() {
        return this.tourRepository.findAll();
    }

    @Transactional
    public Integer deleteTour(Long id) {
        return this.tourRepository.removeTourById(id);
    }
}
