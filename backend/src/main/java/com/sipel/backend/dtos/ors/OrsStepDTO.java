package com.sipel.backend.dtos.ors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Passo individual dentro de uma rota")
public record OrsStepDTO(
    @Schema(description = "Tipo do passo (start, job, end)", example = "job")
    String type,

    @Schema(description = "Localização do passo [longitude, latitude]", example = "[-46.633308, -23.550520]")
    List<Double> location,

    @Schema(description = "ID do trabalho (se aplicável)", example = "1")
    Long id,

    @Schema(description = "Tempo de chegada estimado (timestamp ou segundos)", example = "1678886400")
    Double arrival,

    @Schema(description = "Duração da etapa", example = "300")
    Double duration
) {}