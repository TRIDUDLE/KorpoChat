/*package com.korpochat.backend.service;

import com.korpochat.backend.dto.CreateChannelRequest;
import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.entity.Department;
import com.korpochat.backend.repository.ChannelRepository;
import com.korpochat.backend.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final DepartmentRepository departmentRepository;

    // Wstrzyknięcie obu repozytoriów przez konstruktor
    public ChannelService(ChannelRepository channelRepository, DepartmentRepository departmentRepository) {
        this.channelRepository = channelRepository;
        this.departmentRepository = departmentRepository;
    }

    // Dokładna definicja metody z 1 argumentem
    public Channel createChannel(CreateChannelRequest request) {
        // 1. Inicjalizacja pustego kanału
        Channel newChannel = new Channel();

        // 2. Ustawienie podstawowych pól tekstowych
        newChannel.setName(request.getName());
        newChannel.setDescription(request.getDescription());

        if (request.getChannelType() != null) {
            newChannel.setChannelType(request.getChannelType());
        }

        // 3. Obsługa tagów działów (DepartmentTags)
        if (request.getDepartmentTags() != null && !request.getDepartmentTags().isEmpty()) {
            // Wyszukanie encji działów w bazie danych na podstawie listy nazw stringów
            List<Department> foundDepartments = departmentRepository.findByNameIn(request.getDepartmentTags());

            // Zamiana listy na Set i przypisanie do kanału (zgodnie z relacją ManyToMany)
            newChannel.setDepartments(new HashSet<>(foundDepartments));
        }

        // 4. Zapis gotowego obiektu do bazy danych
        return channelRepository.save(newChannel);
    }
}*/