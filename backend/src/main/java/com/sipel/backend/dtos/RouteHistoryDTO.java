package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Resumo do histórico de execução de rota")
public record RouteHistoryDTO(
    @Schema(description = "ID único da execução", example = "uuid-1234")
    @NotNull
    String id,

    @Schema(description = "ID da equipe", example = "1")
    @NotNull
    Long teamId,

    @Schema(description = "Data da execução", example = "2023-10-25")
    @NotNull
    LocalDate date,

    @Schema(description = "Data de criação formatada", example = "2023-10-25")
    String createdAt
) {}