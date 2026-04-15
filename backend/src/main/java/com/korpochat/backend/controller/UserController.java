package com.korpochat.backend.controller;

import com.korpochat.backend.dto.UpdateUserRequest;
import com.korpochat.backend.dto.UserRequest;
import com.korpochat.backend.dto.UserResponse;
import com.korpochat.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user data endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to fetch a list of all registered users.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to register a new user.
     */
    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest request) {
        UserResponse newUser = userService.addUser(request);
        return ResponseEntity.ok(newUser);
    }

    /**
     * Endpoint to edit an existing user (password and/or role).
     */
    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String username,
            @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userService.updateUser(username, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint to delete a user.
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}