package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados de localização do cliente para rota")
public record ClientDTO(
    @Schema(description = "Número da instalação do cliente", example = "12345")
    @NotNull(message = "O número da instalação é obrigatório")
    Long instalacao,

    @Schema(description = "Latitude do cliente", example = "-23.550520")
    @NotNull(message = "A latitude do cliente é obrigatória")
    Double latitude,

    @Schema(description = "Longitude do cliente", example = "-46.633308")
    @NotNull(message = "A longitude do cliente é obrigatória")
    Double longitude
) {}