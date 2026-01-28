package com.sipel.backend.controllers;

import com.sipel.backend.dtos.MapsRequestDTO;
import com.sipel.backend.services.MapsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/maps")
public class MapsController {

    private final MapsService mapsService;
    private final MeterRegistry meterRegistry;

    public MapsController(MapsService mapsService, MeterRegistry meterRegistry) {
        this.mapsService = mapsService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectUrl(@RequestBody MapsRequestDTO mapsRequest) {
        meterRegistry.counter("business.maps.redirects").increment();
        HttpHeaders headers = mapsService.createMapsUrl(mapsRequest);
        return ResponseEntity.status(302).headers(headers).build();
    }
}
