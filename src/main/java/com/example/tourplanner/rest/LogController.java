package com.example.tourplanner.rest;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.service.LogService;
import org.springframework.web.bind.annotation.*;

@RestController
public class LogController {
    // @Inject
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/log/{id}")
    public Log getLog(@PathVariable Long id) {
        return this.logService.getLog(id);
    }

    @PostMapping("/log")
    public Log createLog(@RequestBody Log log) {
        return this.logService.createLog(log);
    }

    @PutMapping("/log")
    public Log updateLog(@RequestBody Log log) {
        return this.logService.updateLog(log);
    }

    @GetMapping("/logs")
    public Iterable<Log> getLogsByTourId(@RequestParam Long tourId) {
        return this.logService.findLogsByTourId(tourId);
    }

    @DeleteMapping("/log/{id}")
    public Integer deleteLog(@PathVariable Long id) {
        return this.logService.deleteLog(id);
    }
}


// service dazwischen
// service entscheidet ob transaction
// service ruft repo auf