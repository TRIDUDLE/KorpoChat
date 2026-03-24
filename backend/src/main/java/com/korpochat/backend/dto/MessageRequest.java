package com.korpochat.backend.dto;

/**
 * Data Transfer Object for incoming chat messages.
 */
public class MessageRequest {
    private String sender;
    private String text;

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}