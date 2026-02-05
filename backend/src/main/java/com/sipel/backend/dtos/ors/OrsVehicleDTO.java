package com.sipel.backend.dtos.ors;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Definição do veículo para a rota")
public record OrsVehicleDTO(
    @Schema(description = "Identificador do veículo", example = "1")
    Long id,

    @Schema(description = "Perfil de condução", example = "driving-car")
    String profile,

    @Schema(description = "Coordenadas de início [longitude, latitude]", example = "[-46.633308, -23.550520]")
    List<Double> start,

    @Schema(description = "Coordenadas de fim [longitude, latitude]", example = "[-46.633308, -23.550520]")
    List<Double> end
) {}