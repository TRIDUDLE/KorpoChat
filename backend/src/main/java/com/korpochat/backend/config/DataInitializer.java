package com.korpochat.backend.config;

import com.korpochat.backend.entity.Role;
import com.korpochat.backend.entity.Status;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.OFFLINE);
                admin.setCreatedAt(ZonedDateTime.now());
                admin.setTags("System,Admin");
                userRepository.save(admin);
                System.out.println("LOG: Default ADMIN created (admin/admin)");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPasswordHash(passwordEncoder.encode("user"));
                user.setRole(Role.USER);
                user.setStatus(Status.OFFLINE);
                user.setCreatedAt(ZonedDateTime.now());
                user.setTags("Default,Staff");
                userRepository.save(user);
                System.out.println("LOG: Default USER created (user/user)");
            }
        };
    }
}