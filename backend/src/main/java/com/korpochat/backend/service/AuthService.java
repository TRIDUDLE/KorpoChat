package com.korpochat.backend.service;

import com.korpochat.backend.dto.AuthResponse;
import com.korpochat.backend.dto.LoginRequest;
import com.korpochat.backend.dto.LogoutRequest;
import com.korpochat.backend.entity.Status;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Service handling authentication logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates the user and sets status to ONLINE.
     */
    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verify if the provided password matches the stored password hash
        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        user.setStatus(Status.ONLINE);
        user.setLastSeen(ZonedDateTime.now());
        userRepository.save(user);

        // Returning both username and role to match the DTO constructor requirements
        return new AuthResponse(user.getUsername(), user.getRole().name());
    }

    /**
     * Logs out the user and sets status to OFFLINE.
     */
    public void logout(LogoutRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            user.setStatus(Status.OFFLINE);
            user.setLastSeen(ZonedDateTime.now());
            userRepository.save(user);
        });
    }
}