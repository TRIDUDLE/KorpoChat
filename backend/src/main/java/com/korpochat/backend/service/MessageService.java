package com.korpochat.backend.service;

import com.korpochat.backend.entity.Message;
import com.korpochat.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Transactional
    public Message saveMessage(Message message) {
        message.setTimestamp(ZonedDateTime.now());

        if (message.getType() == null || message.getType() == Message.MessageType.CHAT) {
            return messageRepository.save(message);
        }
        return message;
    }
}