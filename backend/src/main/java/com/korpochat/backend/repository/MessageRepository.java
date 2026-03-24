package com.korpochat.backend.repository;

import com.korpochat.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for managing Message entities in the database.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
}