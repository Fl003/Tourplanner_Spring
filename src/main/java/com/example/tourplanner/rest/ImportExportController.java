package com.example.tourplanner.rest;

import com.example.tourplanner.service.ImportExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

@RestController
public class ImportExportController {
    private static final Logger logger = LogManager.getLogger(ImportExportController.class);
    private final ImportExportService importExportService;

    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportTours(@RequestBody List<Long> tourIds) {
        logger.info("Received export request for tour IDs: {}", tourIds);
        try {
            byte[] jsonContent = importExportService.exportTourData(tourIds);
            logger.info("Export successful. Returning response.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tourExport.json")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(jsonContent);
        } catch (JsonProcessingException e) {
            logger.error("Error exporting tour data", e);
            return null;
        }
    }

    @PostMapping("/import")
    public boolean importTours(@RequestBody byte[] jsonContent) {
        logger.info("Received import request");
        boolean result = importExportService.importTours(jsonContent);
        logger.info("Import result: {}", result);
        return result;
    }
}
