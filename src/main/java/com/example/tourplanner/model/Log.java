package com.example.tourplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tourId;
    private Timestamp datetime;
    private String comment;
    private String difficulty;
    private Double totalDistance;
    private Integer totalDuration;
    private Integer rating;
}
