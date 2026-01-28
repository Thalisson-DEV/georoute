package com.sipel.backend.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class MapsService {

    public HttpHeaders createMapsUrl(
            @NotNull(message = "A latitude não pode ser nula")
            Double latitude,
            @NotNull(message = "A longitude não pode ser nula")
            Double longitude
            ) {
        String mapsUrl = String.format("https://www.google.com/maps?q=%s,%s", latitude, longitude);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(mapsUrl));
        return headers;
    }
}
