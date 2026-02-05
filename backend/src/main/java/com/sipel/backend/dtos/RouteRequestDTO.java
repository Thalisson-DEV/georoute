package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Dados para solicitação de otimização de rota")
public record RouteRequestDTO(
    @Schema(description = "ID da equipe que realizará a rota", example = "1")
    @NotNull(message = "O ID da equipe é obrigatório")
    Long teamId,

    @Schema(description = "Lista de clientes/locais a serem visitados")
    @NotEmpty(message = "A lista de clientes não pode estar vazia")
    List<@NotNull @jakarta.validation.Valid ClientDTO> clients,

    @Schema(description = "Latitude atual do técnico (opcional)", example = "-23.550520")
    Double currentLat,

    @Schema(description = "Longitude atual do técnico (opcional)", example = "-46.633308")
    Double currentLon
) {}