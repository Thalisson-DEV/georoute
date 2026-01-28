package com.sipel.backend.dtos;

import java.time.LocalDateTime;

public record RestExceptionResponseDTO(
        LocalDateTime timestamp,
        String message,
        Integer status
) {}
