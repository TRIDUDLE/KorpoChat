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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User addUser(String username, String rawPassword, String roleStr, String tags) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));


            Role role = (roleStr != null) ? Role.valueOf(roleStr.toUpperCase()) : Role.USER;
            user.setRole(role);
            user.setTags(tags);
            user.setStatus(Status.OFFLINE);
            user.setCreatedAt(ZonedDateTime.now());
            return userRepository.save(user);
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
    public void updateTags(String username, String tags) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTags(tags);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}