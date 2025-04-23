package com.example.tourplanner.service;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.repo.LogRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

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

    // TODO: findLogsByTourId

    public Log createLog(Log log) {
        return this.logRepository.save(log);
    }

    public Iterable<Log> getLogs() {
        return this.logRepository.findAll();
    }

    @Transactional
    public Integer deleteLog(Long id) {
        return this.logRepository.removeLogById(id);
    }
}
