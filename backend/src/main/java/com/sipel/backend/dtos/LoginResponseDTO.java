package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta pós login")
public record LoginResponseDTO(
        @Schema(description = "Token JWT")
        String token
) {}
