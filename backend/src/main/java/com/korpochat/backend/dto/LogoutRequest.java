package com.korpochat.backend.dto;

/**
 * DTO for logout payload.
 */
public class LogoutRequest {
    private String username;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}