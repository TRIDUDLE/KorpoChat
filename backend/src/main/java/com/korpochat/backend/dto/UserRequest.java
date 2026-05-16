package com.korpochat.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String role;
    private List<String> tags;
}