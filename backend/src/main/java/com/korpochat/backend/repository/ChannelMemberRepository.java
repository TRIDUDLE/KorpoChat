/*package com.korpochat.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.korpochat.backend.entity.User;

import java.util.UUID;
import java.util.List;

@Repository
public interface ChannelMemberRepository extends JpaRepository<User, UUID> {

    @Query(value = "SELECT CAST(user_id AS VARCHAR) FROM channel_members WHERE channel_id = :channelId", nativeQuery = true)
    List<String> findUserIdsByChannelId(UUID channelId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO channel_members (channel_id, user_id) VALUES (:channelId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addUserToChannel(UUID channelId, UUID userId);

    @Query(value = "SELECT COUNT(*) > 0 FROM channel_members WHERE channel_id = :channelId AND user_id = :userId", nativeQuery = true)
    boolean isUserMemberOfChannel(UUID channelId, UUID userId);
}*/