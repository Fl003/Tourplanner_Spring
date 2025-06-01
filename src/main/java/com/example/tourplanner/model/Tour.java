package com.example.tourplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    public String startingPoint;
    public Double startLat;
    public Double startLng;
    public String destination;
    public Double destinationLat;
    public Double destinationLng;
    public String transportType;
    public Double distance;
    public Double duration;
}
