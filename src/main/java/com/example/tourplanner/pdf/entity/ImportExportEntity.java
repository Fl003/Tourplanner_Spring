package com.example.tourplanner.pdf.entity;

import com.example.tourplanner.model.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportExportEntity {
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
    List<Log> logs;
}
