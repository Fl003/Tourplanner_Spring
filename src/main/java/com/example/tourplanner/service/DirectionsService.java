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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named
public class DirectionsService {
    private static final Logger logger = LogManager.getLogger(DirectionsService.class);
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    public JsonNode getDirections(String transport, Double startLat, Double startLng, Double endLat, Double endLng) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = BASE_URL + TransportType.valueOf(transport).apiValue + "?api_key=" + apiKey + "&start=" + startLng + "," + startLat + "&end=" + endLng + "," + endLat;
            HttpGet request = new HttpGet(url);

            logger.info("Requesting directions from URL: {}", url);

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    logger.info("Directions retrieved successfully");
                    return objectMapper.readTree(jsonResponse);
                } else {
                    logger.error("Failed to fetch route directions. HTTP status code: {}", statusCode);
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("Exception while fetching route directions: ", e);
            return null;
        }
    }
}
