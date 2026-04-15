package com.korpochat.backend.dto;

/**
 * Data Transfer Object for updating an existing user.
 */
public class UpdateUserRequest {
    private String password;
    private String role;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}