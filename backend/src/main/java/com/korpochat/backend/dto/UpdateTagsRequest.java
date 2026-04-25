package com.korpochat.backend.dto;

/**
 * DTO for updating user tags independently.
 */
public class UpdateTagsRequest {
    private String tags;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}