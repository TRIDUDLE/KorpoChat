package com.korpochat.backend.dto;

import java.util.List;

public class CreateChannelRequest {
    private String name;
    private String description;
    private String channelType; // "PRIVATE" dla zamkniętych
    List<String> DepartmentTags; // Twoje "tagi" użytkowników

    // Gettery
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getChannelType() {
        return channelType;
    }

    public List<String> getDepartmentTags() {
        return DepartmentTags;
    }

    // Settery
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public void setDepartmentTags(List<String> memberUsernames) {
        this.DepartmentTags = DepartmentTags;
    }
}
