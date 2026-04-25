package com.korpochat.backend.controller;

import com.korpochat.backend.dto.CreateChannelRequest;
import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public ResponseEntity<?> createChannel(
            // W MVP przekazujemy nazwę admina w nagłówku, by uprościć autoryzację.
            // W produkcji zastąpisz to tokenem JWT.
            @RequestHeader("Requester-Username") String requesterUsername,
            @RequestBody CreateChannelRequest request) {

        try {
            Channel newChannel = channelService.createChannel(requesterUsername, request);
            return ResponseEntity.ok(newChannel);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}