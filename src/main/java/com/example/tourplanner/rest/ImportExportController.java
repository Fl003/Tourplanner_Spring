package com.example.tourplanner.rest;

import com.example.tourplanner.service.ImportExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImportExportController {
    private final ImportExportService importExportService;

    public ImportExportController(ImportExportService importExportService) {
        this.importExportService = importExportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportTours(@RequestBody List<Long> tourIds) {
        try {
            byte[] jsonContent = importExportService.exportTourData(tourIds);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tourExport.json")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(jsonContent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/import")
    public boolean importTours(@RequestBody byte[] jsonContent) {
        return importExportService.importTours(jsonContent);
    }
}
