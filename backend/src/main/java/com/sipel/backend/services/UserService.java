package com.sipel.backend.services;

import com.sipel.backend.domain.Users;
import com.sipel.backend.dtos.RegisterRequestDTO;
import com.sipel.backend.exceptions.UserAlreadyExistsException;
import com.sipel.backend.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(@Valid RegisterRequestDTO registerRequest) {
        if(this.userRepository.existsByEmail((registerRequest.email()))) {
            throw new UserAlreadyExistsException("Usuário já cadastrado com o email:" + registerRequest.email());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.password());
        Users newUser = new Users(registerRequest.email(), encryptedPassword);

        this.userRepository.save(newUser);
    }
}
