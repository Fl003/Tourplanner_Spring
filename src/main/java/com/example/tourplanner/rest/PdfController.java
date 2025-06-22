package com.example.tourplanner.rest;

import com.example.tourplanner.service.PdfService;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PdfController {
    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/download/report/{id}")
    public ResponseEntity<byte[]> generateTourReportPdfFile(@PathVariable Long id) {
        byte[] pdfContent = pdfService.generateTourReportPdfFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tour-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping("/download/report/")
    public ResponseEntity<byte[]> generateSummarizeReportPdfFile() {
        byte[] pdfContent = pdfService.generateSummarizeReportPdfFile();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=summarize-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
