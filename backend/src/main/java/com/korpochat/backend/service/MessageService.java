package com.korpochat.backend.service;

import com.korpochat.backend.dto.MessageRequest;
import com.korpochat.backend.entity.Message;
import com.korpochat.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service handling chat message logic.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        // In a real app, you might want to limit this or sort by timestamp
        return messageRepository.findAll();
    }

    public Message saveMessage(MessageRequest request) {
        Message message = new Message(
                request.getSender(),
                request.getText(),
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
        return messageRepository.save(message);
    }
}