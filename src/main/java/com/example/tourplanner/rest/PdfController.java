package com.example.tourplanner.rest;

import com.example.tourplanner.service.PdfService;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RestController
public class PdfController {
    private static final Logger logger = LogManager.getLogger(PdfController.class);
    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/download/report/{id}")
    public ResponseEntity<byte[]> generateTourReportPdfFile(@PathVariable Long id) {
        logger.info("Received request to generate tour report PDF for tour ID: {}", id);

        byte[] pdfContent = pdfService.generateTourReportPdfFile(id);

        if (pdfContent == null || pdfContent.length == 0) {
            logger.warn("Generated PDF content is empty for tour ID: {}", id);
            return ResponseEntity.noContent().build();
        }

        logger.info("success generated PDF report for tour ID: {}", id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tour-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/download/report/")
    public ResponseEntity<byte[]> generateSummarizeReportPdfFile() {
        logger.info("Received request to generate summarize report PDF");
        byte[] pdfContent = pdfService.generateSummarizeReportPdfFile();

        if (pdfContent == null || pdfContent.length == 0) {
            logger.warn("Generated summarize report PDF content is empty");
            return ResponseEntity.noContent().build();
        }

        logger.info("Successfully generated summarize report PDF");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=summarize-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
