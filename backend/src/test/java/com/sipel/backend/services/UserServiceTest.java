package com.sipel.backend.services;

import com.sipel.backend.domain.Users;
import com.sipel.backend.dtos.RegisterRequestDTO;
import com.sipel.backend.exceptions.UserAlreadyExistsException;
import com.sipel.backend.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDTO request = new RegisterRequestDTO("test@example.com", "password");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        userService.registerUser(request);

        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void shouldThrowExceptionWhenUserExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("test@example.com", "password");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }
}
