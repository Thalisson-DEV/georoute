package com.sipel.backend.services;

import com.sipel.backend.dtos.MapsRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class MapsService {

    public HttpHeaders createMapsUrl(@Valid MapsRequestDTO mapsRequest) {
        String mapsUrl = String.format("https://www.google.com/maps?q=%s,%s", mapsRequest.latitude(), mapsRequest.longitude());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(mapsUrl));
        return headers;
    }
}
