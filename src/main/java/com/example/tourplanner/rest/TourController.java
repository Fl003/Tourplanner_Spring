package com.example.tourplanner.rest;

import com.example.tourplanner.model.Tour;
import com.example.tourplanner.service.TourService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TourController {
    // @Inject
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/tour/{id}")
    public Tour getTour(@PathVariable Long id) {
        return this.tourService.getTour(id);
    }

    @PostMapping("/tour")
    public Tour createTour(@RequestBody Tour tour) {
        return this.tourService.createTour(tour);
    }

    @PutMapping("/tour")
    public Tour updateTour(@RequestBody Tour tour) {
        return this.tourService.updateTour(tour);
    }

    @GetMapping("/tour/latest")
    public Tour getTourLatest() { return this.tourService.getLatest(); }

    @GetMapping("/tours")
    public Iterable<Tour> getTours() {
        return this.tourService.getTours();
    }

    @DeleteMapping("/tour/{id}")
    public Integer deleteTour(@PathVariable Long id) {
        return this.tourService.deleteTour(id);
    }
}


// service dazwischen
// service entscheidet ob transaction
// service ruft repo auf