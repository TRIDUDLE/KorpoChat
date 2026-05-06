package com.korpochat.backend.controller;

import com.korpochat.backend.dto.UserRequest;
import com.korpochat.backend.dto.UpdateUserRequest;
import com.korpochat.backend.dto.UpdateTagsRequest;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserRequest request) {
        // Mapping DTO to service call
        User savedUser = userService.addUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole(),
                request.getTags()
        );
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updatePassword(@PathVariable String username, @RequestBody UpdateUserRequest request) {
        userService.updatePassword(username, request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/tags")
    public ResponseEntity<Void> updateTags(@PathVariable String username, @RequestBody UpdateTagsRequest request) {
        userService.updateTags(username, request.getTags());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}