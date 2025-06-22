package com.example.tourplanner.pdf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TourEntity {
    private String name;
    private String description;
    public String startingPoint;
    public String destination;
    public String transportType;
    public Double distance;
    public Integer hours;
    public Integer minutes;
    public Integer seconds;
    public Integer popularity;
    public Integer childFriendliness;
}
