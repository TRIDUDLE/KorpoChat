package com.korpochat.backend.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for sending user details to the client.
 * Passwords and sensitive data are strictly excluded.
 */
public class UserResponse {

    private UUID id;
    private String username;
    private String role;
    private String status;
    private ZonedDateTime lastSeen;
    private String tags;

    public UserResponse(UUID id, String username, String role, String status, ZonedDateTime lastSeen, String tags) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
        this.lastSeen = lastSeen;
        this.tags = tags;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(ZonedDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getTags() { return tags; }
}