package com.sipel.backend.dtos;

import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.domain.enums.MunicipioEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de retorno da equipe")
public record EquipesResponseDTO(
    @Schema(description = "Identificador da equipe", example = "1")
    Long id,

    @Schema(description = "Nome da equipe", example = "Equipe Alpha")
    String nome,

    @Schema(description = "Latitude base", example = "-23.550520")
    Double latitudeBase,

    @Schema(description = "Longitude base", example = "-46.633308")
    Double longitudeBase,

    @Schema(description = "Setor de atuação", example = "LEITURA")
    SetorEnum setor,

    @Schema(description = "Município de atuação", example = "JUAZEIRO")
    MunicipioEnum municipio
) {}
