package com.sipel.backend.dtos.ors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Resposta da otimização de rota")
public record OrsOptimizationResponseDTO(
    @Schema(description = "Lista de rotas calculadas")
    List<OrsRouteDTO> routes,

    @Schema(description = "Resumo da otimização (custo, distância, duração)")
    OrsSummaryDTO summary
) {}