package com.sipel.backend.dtos;

import com.sipel.backend.domain.enums.SetorEnum;
import com.sipel.backend.domain.enums.MunicipioEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para cadastro de nova equipe")
public record EquipesRequestDTO(
    @Schema(description = "Nome da equipe", example = "Equipe Alpha")
    @NotBlank(message = "O nome da equipe é obrigatório")
    String nome,

    @Schema(description = "Latitude do ponto de partida da equipe", example = "-23.550520")
    @NotNull(message = "A latitude base é obrigatória")
    Double latitudeBase,

    @Schema(description = "Longitude do ponto de partida da equipe", example = "-46.633308")
    @NotNull(message = "A longitude base é obrigatória")
    Double longitudeBase,

    @Schema(description = "Setor de atuação da equipe", example = "LEITURA")
    @NotNull(message = "O setor é obrigatório")
    SetorEnum setor,

    @Schema(description = "Município de atuação da equipe", example = "JUAZEIRO")
    @NotNull(message = "O município é obrigatório")
    MunicipioEnum municipio
) {}
