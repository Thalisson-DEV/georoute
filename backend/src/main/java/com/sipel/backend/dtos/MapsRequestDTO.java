package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para redirecionamento ao mapa")
public record MapsRequestDTO(
        @NotBlank(message = "A latitude não pode estar em branco")
        @NotNull(message = "A latitude não pode estar em branco")
        @Schema(description = "Latitude de destino", example = "-23.550520")
        Double latitude,
        @NotBlank(message = "A longitude não pode estar em branco")
        @NotNull(message = "A longitude não pode estar em branco")
        @Schema(description = "Longitude de destino", example = "-46.633308")
        Double longitude
) {}
