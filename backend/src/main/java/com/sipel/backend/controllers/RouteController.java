package com.sipel.backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sipel.backend.dtos.RouteHistoryDTO;
import com.sipel.backend.dtos.RouteRequestDTO;
import com.sipel.backend.dtos.ors.OrsOptimizationResponseDTO;
import com.sipel.backend.services.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

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
    public ResponseEntity<OrsOptimizationResponseDTO> optimizeRoute(@RequestBody @Valid RouteRequestDTO routeRequest) throws JsonProcessingException {
        OrsOptimizationResponseDTO response = routeService.calculateRoute(routeRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{teamId}")
    @Operation(summary = "Histórico de Rotas", description = "Retorna o histórico de rotas calculadas para uma equipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
    })
    public ResponseEntity<List<RouteHistoryDTO>> getRouteHistory(@PathVariable Long teamId) {
        return ResponseEntity.ok(routeService.getRouteHistory(teamId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da Rota", description = "Retorna o JSON completo de uma rota executada pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes recuperados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar dados da rota")
    })
    public ResponseEntity<OrsOptimizationResponseDTO> getRouteDetails(@PathVariable String id) throws JsonProcessingException {
        return ResponseEntity.ok(routeService.getRouteDetails(id));
    }
}
