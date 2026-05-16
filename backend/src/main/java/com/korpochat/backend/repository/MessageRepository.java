package com.korpochat.backend.repository;

import com.korpochat.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    // Find messages by channel ID and sort them chronologically
    List<Message> findByChannelIdOrderByTimestampAsc(UUID channelId);
}