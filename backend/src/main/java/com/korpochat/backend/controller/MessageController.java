package com.korpochat.backend.controller;

import com.korpochat.backend.dto.MessageRequest;
import com.korpochat.backend.entity.Message;
import com.korpochat.backend.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling chat messages.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request) {
        Message savedMessage = messageService.saveMessage(request);
        return ResponseEntity.ok(savedMessage);
    }
}