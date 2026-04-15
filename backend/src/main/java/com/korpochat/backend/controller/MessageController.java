package com.korpochat.backend.controller;

import com.korpochat.backend.dto.MessageRequest;
import com.korpochat.backend.entity.Message;
import com.korpochat.backend.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling chat messages with WebSocket broadcasting.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request) {
        // 1. Save message to PostgreSQL
        Message savedMessage = messageService.saveMessage(request);

        // 2. BROADCAST: Send the saved message to all users subscribed to /topic/public
        messagingTemplate.convertAndSend("/topic/public", savedMessage);

        // 3. Return response to the sender
        return ResponseEntity.ok(savedMessage);
    }
}