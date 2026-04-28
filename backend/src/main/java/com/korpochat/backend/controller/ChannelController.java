/*package com.korpochat.backend.controller;

import com.korpochat.backend.dto.CreateChannelRequest;
import com.korpochat.backend.entity.Channel;
import com.korpochat.backend.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public ResponseEntity<Channel> createChannel(@RequestBody CreateChannelRequest request) {
        Channel newChannel = channelService.createChannel(request);
        return ResponseEntity.ok(newChannel);
    }
}
*/