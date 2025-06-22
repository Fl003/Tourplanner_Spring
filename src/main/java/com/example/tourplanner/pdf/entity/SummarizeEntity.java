package com.example.tourplanner.pdf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummarizeEntity {
    private String tourName;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
    private Double avgDistance;
    private Double avgRating;
}
