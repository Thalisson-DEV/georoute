package com.sipel.backend.dtos.ors;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO de requisição para otimização no OpenRouteService")
public record OrsOptimizationRequestDTO(
    @Schema(description = "Lista de trabalhos (paradas) a serem realizados")
    List<OrsJobDTO> jobs,

    @Schema(description = "Lista de veículos disponíveis")
    List<OrsVehicleDTO> vehicles
) {}