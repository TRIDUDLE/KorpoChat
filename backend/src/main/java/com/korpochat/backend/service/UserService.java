package com.korpochat.backend.service;

import com.korpochat.backend.dto.UpdateUserRequest;
import com.korpochat.backend.dto.UserResponse;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling user-related business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users from the database and maps them to secure DTOs.
     *
     * @return List of UserResponse objects
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().name(),
                        user.getStatus().name(),
                        user.getLastSeen()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user in the database.
     */
    public UserResponse addUser(com.korpochat.backend.dto.UserRequest request) {
        com.korpochat.backend.entity.User user = new com.korpochat.backend.entity.User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword()); // Add password hashing later
        user.setRole(com.korpochat.backend.entity.Role.valueOf(request.getRole().toUpperCase()));
        user.setStatus(com.korpochat.backend.entity.Status.OFFLINE);

        user = userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getLastSeen()
        );
    }

    /**
     * Updates an existing user's password and/or role.
     */
    public UserResponse updateUser(String username, UpdateUserRequest request) {
        com.korpochat.backend.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Update password only if a new one is provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(request.getPassword()); // Add password hashing later
        }

        // Update role if provided
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            user.setRole(com.korpochat.backend.entity.Role.valueOf(request.getRole().toUpperCase()));
        }

        user = userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getLastSeen()
        );
    }

    /**
     * Deletes a user from the database.
     */
    public void deleteUser(String username) {
        com.korpochat.backend.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        userRepository.delete(user);
    }
}