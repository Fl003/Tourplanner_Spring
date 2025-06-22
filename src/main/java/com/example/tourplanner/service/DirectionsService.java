package com.example.tourplanner.service;

import com.example.tourplanner.model.TransportType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Named;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

@Named
public class DirectionsService {
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    public JsonNode getDirections(String transport, Double startLat, Double startLng, Double endLat, Double endLng) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = BASE_URL + TransportType.valueOf(transport).apiValue + "?api_key=" + apiKey + "&start=" + startLng + "," + startLat + "&end=" + endLng + "," + endLat;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return objectMapper.readTree(jsonResponse);
                } else {
                    System.err.println("Fehler beim Abrufen der Route: " + statusCode);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Abrufen der Route: " + e.getMessage());
            return null;
        }
    }
}
