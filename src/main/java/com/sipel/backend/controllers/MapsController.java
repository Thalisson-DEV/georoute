package com.sipel.backend.controllers;

import com.sipel.backend.dtos.MapsRequestDTO;
import com.sipel.backend.services.MapsService;
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

    public MapsController(MapsService mapsService) {
        this.mapsService = mapsService;
    }

    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectUrl(@RequestBody MapsRequestDTO mapsRequest) {
        HttpHeaders headers = mapsService.createMapsUrl(mapsRequest);
        return ResponseEntity.status(302).headers(headers).build();
    }
}
