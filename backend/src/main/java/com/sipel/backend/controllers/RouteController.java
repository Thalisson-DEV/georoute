package com.sipel.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sipel.backend.dtos.RouteRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationResponseDTO;
import com.sipel.backend.services.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Rotas", description = "Gerenciamento e otimização de rotas")
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/optimize")
    @Operation(summary = "Calcular rota otimizada", description = "Calcula a melhor rota para uma equipe visitar uma lista de clientes. " +
            "Se a localização atual não for fornecida, utiliza a base da equipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rota calculada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao calcular rota")
    })
    public ResponseEntity<OrsOptimizationResponseDTO> optimizeRoute(@RequestBody RouteRequestDTO routeRequest) throws IOException {
        OrsOptimizationResponseDTO response;
        response = routeService.calculateRoute(routeRequest);
        return ResponseEntity.ok(response);
    }
}