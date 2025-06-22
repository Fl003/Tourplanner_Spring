package com.example.tourplanner.pdf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class LogEntity {
    private LocalDate date;
    private LocalTime time;
    private String comment;
    private String difficulty;
    private Double totalDistance;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
    private Integer rating;
}
