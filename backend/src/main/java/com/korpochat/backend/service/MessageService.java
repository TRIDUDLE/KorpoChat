package com.korpochat.backend.service;

import com.korpochat.backend.dto.MessageRequest;
import com.korpochat.backend.entity.Message;
import com.korpochat.backend.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service handling chat message logic and WebSocket broadcasting.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;


    private final SimpMessagingTemplate messagingTemplate;


    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message saveMessage(MessageRequest request) {
        Message message = new Message(
                request.getSender(),
                request.getText(),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );

        // Save to PostgreSQL database
        Message savedMessage = messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/public", savedMessage);

        return savedMessage;
    }
}