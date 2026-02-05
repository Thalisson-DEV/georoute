package com.sipel.backend.dtos.ors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Detalhes de uma rota específica")
public record OrsRouteDTO(
    @Schema(description = "ID do veículo associado a esta rota", example = "1")
    Long vehicle,

    @Schema(description = "Passos/Etapas da rota")
    List<OrsStepDTO> steps
) {}