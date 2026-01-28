package com.sipel.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipel.backend.dtos.AuthenticationRequestDTO;
import com.sipel.backend.dtos.LoginResponseDTO;
import com.sipel.backend.services.AuthenticationService;
import com.sipel.backend.services.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Should return token when login is successful")
    void shouldReturnTokenOnLogin() throws Exception {
        AuthenticationRequestDTO request = new AuthenticationRequestDTO("test@example.com", "password");
        LoginResponseDTO response = new LoginResponseDTO("mock-token");

        when(authenticationService.login(any(AuthenticationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token"));
    }
}
