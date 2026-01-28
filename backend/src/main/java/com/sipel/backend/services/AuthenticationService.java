package com.sipel.backend.services;

import com.sipel.backend.domain.Users;
import com.sipel.backend.dtos.AuthenticationRequestDTO;
import com.sipel.backend.dtos.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO login(@Valid AuthenticationRequestDTO authenticationRequest) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((Users) Objects.requireNonNull(auth.getPrincipal()));

        return new LoginResponseDTO(token);
    }

}
