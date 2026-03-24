package com.korpochat.backend.service;

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
}