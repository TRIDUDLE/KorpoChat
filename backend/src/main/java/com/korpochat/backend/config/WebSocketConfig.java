package com.korpochat.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration for WebSocket message broker.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint that clients will use to connect to our WebSocket server
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allows cross-origin requests (CORS)
                .withSockJS(); // Fallback mechanism if raw WebSockets are not supported
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enables a simple in-memory broker for broadcasting messages
        registry.enableSimpleBroker("/topic");

        // Prefix for messages sent FROM the client TO the server
        registry.setApplicationDestinationPrefixes("/app");
    }
}