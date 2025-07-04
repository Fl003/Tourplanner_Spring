package com.example.tourplanner.model;

public enum TransportType {
    Car("driving-car"),
    Bus("driving-hgv"),
    Bike("cycling-regular"),
    Walking("foot-walking");

    public final String apiValue;

    TransportType(String apiValue) {
        this.apiValue = apiValue;
    }
}
