package com.korpochat.backend.repository;

import com.korpochat.backend.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    Optional<Channel> findByName(String name);
    List<Channel> findAllByNameIn(Collection<String> names);

    @Query(value = "SELECT c.* FROM channels c JOIN channel_members cm ON c.id = cm.channel_id WHERE cm.user_id = :userId ORDER BY c.name",
           nativeQuery = true)
    List<Channel> findChannelsByUserId(@Param("userId") UUID userId);
}
