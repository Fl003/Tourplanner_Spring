package com.example.tourplanner.service;

import com.example.tourplanner.model.Tour;
import com.example.tourplanner.repo.TourRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Tour getLatest() {
        return this.tourRepository.findTopByOrderByIdDesc();
    }

    public Iterable<Tour> getTours() {
        return this.tourRepository.findAll();
    }

    public Tour updateTour(Tour updatedTour) {
        Optional<Tour> existing = tourRepository.findById(updatedTour.getId());
        if (existing.isPresent()) {
            Tour tour = existing.get();
            tour.setName(updatedTour.getName());
            tour.setDescription(updatedTour.getDescription());
            tour.setStartingPoint(updatedTour.getStartingPoint());
            tour.setStartLat(updatedTour.getStartLat());
            tour.setStartLng(updatedTour.getStartLng());
            tour.setDestination(updatedTour.getDestination());
            tour.setDestinationLat(updatedTour.getDestinationLat());
            tour.setDestinationLng(updatedTour.getDestinationLng());
            tour.setTransportType(updatedTour.getTransportType());
            tour.setDistance(updatedTour.getDistance());
            tour.setDuration(updatedTour.getDuration());

            return tourRepository.save(tour); // Update in DB
        }
        return null;
    }

    @Transactional
    public Integer deleteTour(Long id) {
        return this.tourRepository.removeTourById(id);
    }
}
