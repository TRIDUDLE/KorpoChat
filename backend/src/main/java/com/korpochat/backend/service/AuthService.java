package com.korpochat.backend.service;

import com.korpochat.backend.entity.User;
import com.korpochat.backend.entity.Status;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Match raw password with hash from DB
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        user.setStatus(Status.ONLINE);
        user.setLastSeen(ZonedDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setStatus(Status.OFFLINE);
            user.setLastSeen(ZonedDateTime.now());
            userRepository.save(user);
        });
    }
}