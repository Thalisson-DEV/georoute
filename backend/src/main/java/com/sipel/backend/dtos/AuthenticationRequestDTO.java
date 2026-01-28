package com.sipel.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para autenticação na aplicação")
public record AuthenticationRequestDTO(
        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "Email invalido")
        @Schema(description = "Email de login", example = "username@example.com")
        String email,
        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 5, max = 12, message = "A senha deve ter entre 5 a 12 caracteres.")
        @Schema(description = "Senha de login", example = "admin123")
        String password
) {}
