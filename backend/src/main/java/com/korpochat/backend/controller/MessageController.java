package com.korpochat.backend.controller;

import com.korpochat.backend.entity.Message;
import com.korpochat.backend.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getChatHistory() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PostMapping
    public ResponseEntity<Message> postMessage(@RequestBody Message message) {
        Message saved = messageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/public", saved);
        return ResponseEntity.ok(saved);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage) {
        Message processedMessage = messageService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/topic/public", processedMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }
}