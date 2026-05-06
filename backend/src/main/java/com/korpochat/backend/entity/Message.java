package com.korpochat.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "channel_id")
    private UUID channelId;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String text;

    private ZonedDateTime timestamp;

    @Transient
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}