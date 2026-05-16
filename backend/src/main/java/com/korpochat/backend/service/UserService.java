package com.korpochat.backend.service;

import com.korpochat.backend.entity.User;
import com.korpochat.backend.entity.Role;
import com.korpochat.backend.entity.Status;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagService tagService;
    private final ChannelService channelService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       TagService tagService, ChannelService channelService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tagService = tagService;
        this.channelService = channelService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User addUser(String username, String rawPassword, String roleStr, List<String> tags) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));

            Role role = (roleStr != null) ? Role.valueOf(roleStr.toUpperCase()) : Role.USER;
            user.setRole(role);
            user.setTags(tagService.validateAndFormatTags(tags));
            user.setStatus(Status.OFFLINE);
            user.setCreatedAt(ZonedDateTime.now());

            User savedUser = userRepository.save(user);
            channelService.syncUserChannels(savedUser);
            return savedUser;
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR IN ADDUSER: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void updatePassword(String username, String newRawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        user.setLastSeen(ZonedDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateTags(String username, List<String> tags) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTags(tagService.validateAndFormatTags(tags));
        User savedUser = userRepository.save(user);
        channelService.syncUserChannels(savedUser);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<com.korpochat.backend.entity.Channel> getChannelsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        channelService.syncUserChannels(user);
        return channelService.getChannelsForUser(user.getId());
    }
}
