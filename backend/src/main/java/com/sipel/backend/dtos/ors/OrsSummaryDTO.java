package com.sipel.backend.dtos.ors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Resumo estatístico da rota")
public record OrsSummaryDTO(
    @Schema(description = "Custo total calculado", example = "1200.50")
    Double cost,

    @Schema(description = "Duração total em segundos", example = "3600")
    Double duration,

    @Schema(description = "Distância total em metros", example = "50000")
    Double distance
) {}