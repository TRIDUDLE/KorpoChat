package com.korpochat.backend.dto;

/**
 * Data Transfer Object representing the outgoing authentication result.
 */
public class AuthResponse {

    private String username;
    private String role;

    public AuthResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}