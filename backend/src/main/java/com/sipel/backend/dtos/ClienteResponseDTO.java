package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta do cliente")
public record ClienteResponseDTO(
        @Schema(description = "Número da instalação", example = "12345678")
        Long instalacao,
        @Schema(description = "Nome do cliente", example = "João da Silva")
        String nomeCliente,
        @Schema(description = "Latitude", example = "-23.550520")
        Double latitude,
        @Schema(description = "Longitude", example = "-46.633308")
        Double longitude
) {}
