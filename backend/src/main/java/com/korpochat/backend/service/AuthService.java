package com.korpochat.backend.service;

import com.korpochat.backend.dto.AuthResponse;
import com.korpochat.backend.dto.LoginRequest;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for user authentication logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user based on provided credentials.
     */
    public AuthResponse authenticate(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPasswordHash().equals(request.getPassword())) {
                return new AuthResponse(user.getUsername(), user.getRole().name());
            }
        }

        throw new RuntimeException("Invalid username or password");
    }
}