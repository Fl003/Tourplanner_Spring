package com.example.tourplanner.service;

import com.example.tourplanner.model.Tour;
import com.example.tourplanner.repo.TourRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.DocumentException;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Named
public class PdfService {
    private final TourRepository tourRepository;

    @Autowired
    @Qualifier("pdfTemplateEngine")
    private TemplateEngine pdfTemplateEngine;

    public PdfService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public byte[] generateTourReportPdfFile(Long id) {
        Tour tour = this.tourRepository.findById(id).orElse(null);

        if (tour == null) {
            System.out.println("Tour not found");
            return null;
        }

        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("tour", tour);
        context.setVariables(data);

        String htmlContent = pdfTemplateEngine.process("tour_report_template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();

            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Fehler bei der PDF-Generierung: " + e.getMessage(), e);
        }
    }
}
