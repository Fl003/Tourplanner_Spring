package com.example.tourplanner.service;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.model.Tour;
import com.example.tourplanner.pdf.entity.LogEntity;
import com.example.tourplanner.pdf.entity.SummarizeEntity;
import com.example.tourplanner.pdf.entity.TourEntity;
import com.example.tourplanner.repo.LogRepository;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Named
public class PdfService {
    private static final Logger logger = LogManager.getLogger(PdfService.class);
    private final TourRepository tourRepository;
    private final LogRepository logRepository;

    @Autowired
    @Qualifier("pdfTemplateEngine")
    private TemplateEngine pdfTemplateEngine;

    public PdfService(TourRepository tourRepository, LogRepository logRepository) {
        this.tourRepository = tourRepository;
        this.logRepository = logRepository;
    }

    public byte[] generateTourReportPdfFile(Long id) {
        logger.info("Generating tour report PDF for tour ID: {}", id);

        Tour tour = this.tourRepository.findById(id).orElse(null);

        if (tour == null) {
            logger.warn("Tour not found for ID: {}", id);
            return null;
        }

        List<Log> logs = this.logRepository.findByTourId(id);

        int totalTime = tour.getDuration().intValue();
        int hours =  totalTime / 3600;
        int minutes = totalTime % 3600 / 60;
        int seconds = totalTime % 60;
        TourEntity tourEntity = new TourEntity(
                tour.getName(),
                tour.getDescription(),
                tour.getStartingPoint(),
                tour.getDestination(),
                tour.getTransportType(),
                tour.getDistance() / 1000,
                hours,
                minutes,
                seconds,
                getPopularity(logs),
                getChildFriendliness(logs)
        );

        List<LogEntity> logEntities = new ArrayList<>();
        for (Log log : logs) {
            LocalDate date = log.getDatetime().toLocalDateTime().toLocalDate();
            LocalTime time = log.getDatetime().toLocalDateTime().toLocalTime();

            totalTime = tour.getDuration().intValue();
            hours =  totalTime / 3600;
            minutes = totalTime % 3600 / 60;
            seconds = totalTime % 60;

            LogEntity logEntity = new LogEntity(
                    date,
                    time,
                    log.getComment(),
                    log.getDifficulty(),
                    log.getTotalDistance() / 1000,
                    hours,
                    minutes,
                    seconds,
                    log.getRating()
            );
            logEntities.add(logEntity);
        }

        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("tour", tourEntity);
        data.put("logs", logEntities);
        context.setVariables(data);

        String htmlContent = pdfTemplateEngine.process("tour_report_template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();

            logger.info("Successfully generated tour report PDF for tour ID: {}", id);
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("IO Exception during PDF generation for tour ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("FPDF-Generierung error: " + e.getMessage(), e);
        } catch (com.itextpdf.text.DocumentException e) {
            logger.error("Document Exception during PDF generation for tour ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public byte[] generateSummarizeReportPdfFile() {
        logger.info("Generating summarize report PDF");
        Iterable<Tour> tours = this.tourRepository.findAll();

        List<SummarizeEntity> summarizeEntities = new ArrayList<>();
        if (tours.iterator().hasNext()) {
            for (Tour tour : tours) {
                List<Log> logs = this.logRepository.findByTourId(tour.getId());
                if (logs == null || logs.isEmpty()) {
                    summarizeEntities.add(new SummarizeEntity(tour.getName(), 0, 0, 0, 0.0, 0.0));
                } else {
                    double timeOfAllLogs = 0, distanceOfAllLogs = 0, ratingOfAllLogs = 0;
                    for (Log log : logs) {
                        timeOfAllLogs += log.getTotalDuration();
                        distanceOfAllLogs += log.getTotalDistance();
                        ratingOfAllLogs += log.getRating();
                    }
                    int logsCount = logs.size();
                    double avgTime = timeOfAllLogs / logsCount;
                    int hours = (int) Math.floor(avgTime / 3600);
                    int minutes = (int) Math.floor(avgTime % 3600 / 60);
                    int seconds = (int) Math.floor(avgTime % 60);
                    summarizeEntities.add(new SummarizeEntity(tour.getName(), hours, minutes, seconds, distanceOfAllLogs / logsCount, ratingOfAllLogs / logsCount));
                }
            }
        }

        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("items", summarizeEntities);
        data.put("itemsCount", summarizeEntities.size());
        context.setVariables(data);

        String htmlContent = pdfTemplateEngine.process("summarize_report_template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();

            logger.info("Successfully generated summarize report PDF with {} items", summarizeEntities.size());
            return outputStream.toByteArray();
        } catch (IOException | com.itextpdf.text.DocumentException e) {
            logger.error("Exception during summarize PDF generation: {}", e.getMessage(), e);
            throw new RuntimeException("PDF-Generierung error: " + e.getMessage(), e);
        }
    }

    public int getPopularity(List<Log> logs) {
        // count: <=1, <=3, <=6, <=10, ->
        // stars:  1,   2,   3,    4,   5
        if (logs == null) return 1;
        int logsCount = logs.size();
        if (logsCount <= 1) return 1;
        if (logsCount <= 3) return 2;
        if (logsCount <= 6) return 3;
        if (logsCount <= 10) return 4;
        return 5;
    }

    public int getChildFriendliness(List<Log> logs) {
        // Difficulty: Easy = 2, Medium = 1, Difficult = 0
        // Total Time: <30min = 2, 30-90min = 1, >90min = 0
        // Distance: <5km = 2, 5-10km = 1, > 10km = 0
        // avg -> 1-5 stars
        if (logs == null || logs.size() == 0) return 1;
        int points = 0;
        for (Log log : logs) {
            switch(log.getDifficulty()) {
                case "Easy" -> points += 2;
                case "Medium" -> points += 1;
                default -> points += 0;
            }

            int time = log.getTotalDuration();
            if(time < 1800) points += 2;
            else if (time < 5400) points += 1;

            double distance = log.getTotalDistance();
            if (distance < 5000) points += 2;
            else if (distance < 10000) points += 1;
        }

        double avg = points / logs.size();
        return (int) Math.round(avg / 6 * 5);
    }
}
