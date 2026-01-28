package com.sipel.backend.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapsServiceTest {

    private final MapsService mapsService = new MapsService();

    @Test
    @DisplayName("Should create Google Maps URL with correct coordinates")
    void shouldCreateCorrectUrl() {
        Double lat = -23.5505;
        Double lon = -46.6333;

        HttpHeaders headers = mapsService.createMapsUrl(lat, lon);

        URI expectedUri = URI.create("https://www.google.com/maps?q=-23.5505,-46.6333");
        assertEquals(expectedUri, headers.getLocation());
    }
}
