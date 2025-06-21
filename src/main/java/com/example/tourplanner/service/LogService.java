package com.example.tourplanner.service;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.repo.LogRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.Optional;

// Business Logic Layer

@Named
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public Log getLog(Long id) {
        return this.logRepository.findById(id).orElse(null);
    }

    public Iterable<Log> findLogsByTourId(Long tourId) {
        return this.logRepository.findByTourId(tourId);
    }

    public Log createLog(Log log) {
        return this.logRepository.save(log);
    }

    public Log updateLog(Log updatedLog) {
        Optional<Log> existing = logRepository.findById(updatedLog.getId());
        if (existing.isPresent()) {
            Log log = existing.get();
            log.setDatetime(updatedLog.getDatetime());
            log.setComment(updatedLog.getComment());
            log.setDifficulty(updatedLog.getDifficulty());
            log.setTotalDistance(updatedLog.getTotalDistance());
            log.setTotalDuration(updatedLog.getTotalDuration());
            log.setRating(updatedLog.getRating());

            return logRepository.save(log); // Update in DB
        }
        return null;
    }
    
    @Transactional
    public Integer deleteLog(Long id) {
        return this.logRepository.removeLogById(id);
    }
}
