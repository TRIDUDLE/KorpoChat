package com.korpochat.backend.service;

import com.korpochat.backend.entity.Message;
import com.korpochat.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID; 

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getMessagesByChannelId(UUID channelId) {
        return messageRepository.findByChannelIdOrderByTimestampAsc(channelId);
    }

    @Transactional
    public Message saveMessage(Message message) {
        // Fallback: Set timestamp if it wasn't set by the Controller
        if (message.getTimestamp() == null) {
            message.setTimestamp(ZonedDateTime.now());
        }

        // Save to DB only if it's a standard CHAT message 
        if (message.getType() == null || message.getType() == Message.MessageType.CHAT) {
            return messageRepository.save(message);
        }
        
        return message; 
    }
}