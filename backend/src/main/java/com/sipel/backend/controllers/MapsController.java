package com.sipel.backend.controllers;

import com.sipel.backend.dtos.MapsRequestDTO;
import com.sipel.backend.services.MapsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/maps")
@Tag(name = "Mapas", description = "Integração com serviços de mapas (Google Maps)")
public class MapsController {

    private final MapsService mapsService;
    private final MeterRegistry meterRegistry;

    public MapsController(MapsService mapsService, MeterRegistry meterRegistry) {
        this.mapsService = mapsService;
        this.meterRegistry = meterRegistry;
    }

    @Operation(summary = "Redirecionar para Mapa", description = "Gera uma URL do Google Maps com base nas coordenadas e redireciona o cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectUrl(@RequestBody MapsRequestDTO mapsRequest) {
        meterRegistry.counter("business.maps.redirects").increment();
        HttpHeaders headers = mapsService.createMapsUrl(mapsRequest);
        return ResponseEntity.status(302).headers(headers).build();
    }
}
