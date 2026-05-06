package com.korpochat.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user tags.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTagsRequest {
    private String tags;
}