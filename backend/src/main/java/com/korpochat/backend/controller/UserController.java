package com.korpochat.backend.controller;

import com.korpochat.backend.dto.UserRequest;
import com.korpochat.backend.dto.UserResponse;
import com.korpochat.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}