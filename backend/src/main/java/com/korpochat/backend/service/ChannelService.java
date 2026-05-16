package com.korpochat.backend.service;

import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.entity.Role;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.ChannelRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final JdbcTemplate jdbcTemplate;

    public ChannelService(ChannelRepository channelRepository, JdbcTemplate jdbcTemplate) {
        this.channelRepository = channelRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Channel createChannelIfMissing(String name, String description, String channelType) {
        return channelRepository.findByName(name)
                .orElseGet(() -> {
                    Channel channel = new Channel();
                    channel.setId(UUID.randomUUID());
                    channel.setName(name);
                    channel.setDescription(description);
                    channel.setChannelType(channelType);
                    channel.setCreatedAt(ZonedDateTime.now());
                    return channelRepository.save(channel);
                });
    }

    public Optional<Channel> getChannelByName(String name) {
        return channelRepository.findByName(name);
    }

    @Transactional
    public void syncUserChannels(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        Set<String> channelNames = new LinkedHashSet<>();
        channelNames.add("#główny");

        if (user.getRole() == Role.ADMIN) {
            channelNames.add("#admin");
        }

        if (user.getTags() != null && !user.getTags().isBlank()) {
            Arrays.stream(user.getTags().split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .map(tag -> "#" + tag)
                    .forEach(channelNames::add);
        }

        List<Channel> desiredChannels = channelRepository.findAllByNameIn(channelNames);

        // make sure all expected channels exist, create missing ones for tags
        for (String channelName : channelNames) {
            if (desiredChannels.stream().noneMatch(channel -> channel.getName().equals(channelName))) {
                Channel created = createChannelIfMissing(channelName,
                        "Kanał automatycznie utworzony dla " + channelName,
                        "PUBLIC");
                desiredChannels.add(created);
            }
        }

        Set<UUID> desiredIds = desiredChannels.stream()
                .map(Channel::getId)
                .collect(Collectors.toSet());

        List<Channel> currentChannels = channelRepository.findChannelsByUserId(user.getId());
        Set<UUID> currentIds = currentChannels.stream()
                .map(Channel::getId)
                .collect(Collectors.toSet());

        for (UUID desiredId : desiredIds) {
            if (!currentIds.contains(desiredId)) {
                addUserToChannel(user.getId(), desiredId);
            }
        }

        for (UUID currentId : currentIds) {
            if (!desiredIds.contains(currentId)) {
                removeUserFromChannel(user.getId(), currentId);
            }
        }
    }

    public void addUserToChannel(UUID userId, UUID channelId) {
        jdbcTemplate.update(
                "INSERT INTO channel_members (channel_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                channelId,
                userId
        );
    }

    public void removeUserFromChannel(UUID userId, UUID channelId) {
        jdbcTemplate.update(
                "DELETE FROM channel_members WHERE channel_id = ? AND user_id = ?",
                channelId,
                userId
        );
    }

    public List<Channel> getChannelsForUser(UUID userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return channelRepository.findChannelsByUserId(userId);
    }
}
