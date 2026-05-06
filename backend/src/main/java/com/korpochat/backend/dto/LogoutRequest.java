package com.korpochat.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user logout requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String username;
}