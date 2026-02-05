package com.sipel.backend.dtos.ors;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Representação de um trabalho/parada na rota")
public record OrsJobDTO(
    @Schema(description = "Identificador único do trabalho (geralmente ID do cliente)", example = "1")
    Long id,

    @Schema(description = "Duração do serviço em segundos", example = "300")
    Integer service,

    @Schema(description = "Coordenadas [longitude, latitude]", example = "[-46.633308, -23.550520]")
    List<Double> location
) {}