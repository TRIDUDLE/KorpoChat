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
import java.util.UUID;

@RestController
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    // REST: Explicitly mapped path for fetching history
    @GetMapping("/api/messages/{channelId}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable UUID channelId) {
        return ResponseEntity.ok(messageService.getMessagesByChannelId(channelId));
    }

    // REST: Explicitly mapped path for HTTP fallback
    @PostMapping("/api/messages")
    public ResponseEntity<Message> postMessage(@RequestBody Message message) {
        Message saved = messageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/channel/" + saved.getChannelId(), saved);
        return ResponseEntity.ok(saved);
    }

    // WEBSOCKET: Now accurately listens to /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage) {
        Message processedMessage = messageService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/topic/channel/" + processedMessage.getChannelId(), processedMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }
        messagingTemplate.convertAndSend("/topic/channel/" + chatMessage.getChannelId(), chatMessage);
    }
}