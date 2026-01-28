package com.sipel.backend.services;

import com.sipel.backend.domain.Users;
import com.sipel.backend.dtos.AuthenticationRequestDTO;
import com.sipel.backend.dtos.LoginResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should login successfully and return token")
    void shouldLoginSuccessfully() {
        // Arrange
        AuthenticationRequestDTO request = new AuthenticationRequestDTO("test@example.com", "password");
        Users user = new Users();
        user.setEmail("test@example.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(user)).thenReturn("mock-token");

        // Act
        LoginResponseDTO result = authenticationService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals("mock-token", result.token());
    }

    // Helper to mock Authentication since it's an interface
    private Authentication mock(Class<Authentication> classToMock) {
        return org.mockito.Mockito.mock(classToMock);
    }
}
