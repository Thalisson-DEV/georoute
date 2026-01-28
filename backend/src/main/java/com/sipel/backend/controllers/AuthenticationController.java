package com.sipel.backend.controllers;

import com.sipel.backend.dtos.AuthenticationRequestDTO;
import com.sipel.backend.dtos.LoginResponseDTO;
import com.sipel.backend.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Login de Usuário", description = "Autentica um usuário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<@NonNull LoginResponseDTO> login(@RequestBody AuthenticationRequestDTO authenticationRequest) {
        LoginResponseDTO authenticationResponse = this.authenticationService.login(authenticationRequest);

        return ResponseEntity.ok().body(authenticationResponse);
    }
}
