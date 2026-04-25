package com.korpochat.backend.service;

import com.korpochat.backend.dto.CreateChannelRequest;
import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.entity.Role;
import com.korpochat.backend.entity.User;
import com.korpochat.backend.repository.ChannelRepository;
import com.korpochat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Channel createChannel(String requesterUsername, CreateChannelRequest request) {
        // 1. Sprawdzenie, czy wykonujący żądanie to ADMIN
        User adminUser = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika wnoszącego o akcję."));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Brak uprawnień. Tylko administrator może tworzyć nowe kanały.");
        }

        // 2. Tworzenie kanału
        Channel channel = new Channel();
        channel.setName(request.getName());
        channel.setDescription(request.getDescription());
        channel.setChannelType(request.getChannelType() != null ? request.getChannelType() : "PRIVATE");

        // Krok 3. Automatyczne dodawanie członków na podstawie tagów/działów
        Set<User> members = new HashSet<>();
        members.add(adminUser); // Zawsze dodajemy admina tworzącego kanał

        if (request.getDepartmentTags() != null && !request.getDepartmentTags().isEmpty()) {
            // Spring automatycznie wyciągnie wszystkich ludzi powiązanych z przesłanymi tagami
            List<User> usersWithTags = userRepository.findByDepartments_NameIn(request.getDepartmentTags());
            members.addAll(usersWithTags);
        }

        channel.setMembers(members);
        return channelRepository.save(channel);
    }
}