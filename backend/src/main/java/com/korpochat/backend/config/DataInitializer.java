package com.korpochat.backend.config;

import com.korpochat.backend.entity.Role;
import com.korpochat.backend.entity.Status;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.UserRepository;
import com.korpochat.backend.service.ChannelService;
import com.korpochat.backend.service.TagService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   TagService tagService,
                                   ChannelService channelService,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            channelService.createChannelIfMissing("#główny",
                    "Główny kanał publiczny",
                    "PUBLIC");

            tagService.createTagIfMissing("admin");
            tagService.createTagIfMissing("default");
            tagService.createTagIfMissing("user");

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.OFFLINE);
                admin.setCreatedAt(ZonedDateTime.now());
                admin.setTags("admin,default");
                User savedAdmin = userRepository.save(admin);
                channelService.syncUserChannels(savedAdmin);
                System.out.println("LOG: Default ADMIN created (admin/admin)");
            }
        };
    }
}