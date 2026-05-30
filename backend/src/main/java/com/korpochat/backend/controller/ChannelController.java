package com.korpochat.backend.controller;

import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.repository.ChannelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelRepository channelRepository;

    public ChannelController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @GetMapping
    public List<Channel> getChannels() {
        return channelRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createChannel(@RequestBody Map<String, String> request) {
        String name = request.get("name").toLowerCase().replace(" ", "-");
        if (channelRepository.existsByName(name)) {
            return ResponseEntity.badRequest().body("Kanał już istnieje");
        }
        Channel channel = new Channel();
        channel.setName(name);
        return ResponseEntity.ok(channelRepository.save(channel));
    }
}
