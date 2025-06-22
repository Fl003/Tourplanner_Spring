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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Named
public class PdfService {
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
        Tour tour = this.tourRepository.findById(id).orElse(null);

        if (tour == null) {
            System.out.println("Tour not found");
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

            return outputStream.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Fehler bei der PDF-Generierung: " + e.getMessage(), e);
        }
    }

    public byte[] generateSummarizeReportPdfFile() {
        Iterable<Tour> tours = this.tourRepository.findAll();

        if (!tours.iterator().hasNext()) {
            System.out.println("No tours found");
            return null;
        }

        List<SummarizeEntity> summarizeEntities = new ArrayList<>();
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

        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("items", summarizeEntities);
        context.setVariables(data);

        String htmlContent = pdfTemplateEngine.process("summarize_report_template", context);

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
