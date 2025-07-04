package com.example.tourplanner.service;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.model.Tour;
import com.example.tourplanner.pdf.entity.ImportExportEntity;
import com.example.tourplanner.repo.LogRepository;
import com.example.tourplanner.repo.TourRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.inject.Named;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Named
public class ImportExportService {
    private final TourRepository tourRepository;
    private final LogRepository logRepository;
    private ObjectMapper mapper = new ObjectMapper();

    public ImportExportService(TourRepository tourRepository, LogRepository logRepository) {
        this.tourRepository = tourRepository;
        this.logRepository = logRepository;
    }

    public byte[] exportTourData(List<Long> tourIds) throws JsonProcessingException {
        List<ImportExportEntity> exportList = new ArrayList<>();

        for (Long id : tourIds) {
            Tour tour = tourRepository.findById(id).orElse(null);
            if (tour == null) continue;
            List<Log> logs = logRepository.findByTourId(id);
            ImportExportEntity importExportEntity = new ImportExportEntity(
                tour.getName(),
                    tour.getDescription(),
                    tour.getStartingPoint(),
                    tour.getStartLat(),
                    tour.getStartLng(),
                    tour.getDestination(),
                    tour.getDestinationLat(),
                    tour.getDestinationLng(),
                    tour.getTransportType(),
                    tour.getDistance(),
                    tour.getDuration(),
                    logs
            );
            exportList.add(importExportEntity);
        }

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsBytes(exportList);
    }

    public boolean importTours(byte[] jsonContent) {
        try {
            List<ImportExportEntity> tours = mapper.readValue(jsonContent, new TypeReference<>() {});

            // Jetzt kannst du mit den Touren arbeiten:
            for (ImportExportEntity tour : tours) {
                // tour speichern
                Tour newTour = new Tour();
                newTour.setName(tour.getName());
                newTour.setDescription(tour.getDescription());
                newTour.setStartingPoint(tour.getStartingPoint());
                newTour.setStartLat(tour.getStartLat());
                newTour.setStartLng(tour.getStartLng());
                newTour.setDestination(tour.getDestination());
                newTour.setDestinationLat(tour.getDestinationLat());
                newTour.setDestinationLng(tour.getDestinationLng());
                newTour.setTransportType(tour.getTransportType());
                newTour.setDistance(tour.getDistance());
                newTour.setDuration(tour.getDuration());
                Tour dbTour = tourRepository.save(newTour);

                System.out.println(dbTour.getId());
                // logs speichern
                for (Log log : tour.getLogs()) {
                    Log newLog = new Log();
                    newLog.setTourId(dbTour.getId());
                    newLog.setDifficulty(log.getDifficulty());
                    newLog.setComment(log.getComment());
                    newLog.setDatetime(log.getDatetime());
                    newLog.setRating(log.getRating());
                    newLog.setTotalDistance(log.getTotalDistance());
                    newLog.setTotalDuration(log.getTotalDuration());
                    logRepository.save(newLog);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
