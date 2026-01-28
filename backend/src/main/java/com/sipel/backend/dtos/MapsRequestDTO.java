package com.sipel.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MapsRequestDTO(
        @NotBlank(message = "A latitude não pode estar em branco")
        @NotNull(message = "A latitude não pode estar em branco")
        Double latitude,
        @NotBlank(message = "A longitude não pode estar em branco")
        @NotNull(message = "A longitude não pode estar em branco")
        Double longitude
) {}
