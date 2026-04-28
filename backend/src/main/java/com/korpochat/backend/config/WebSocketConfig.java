package com.korpochat.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Rejestracja endpointu zgodnego z tym, czego szuka aplikacja kliencka w pliku app.js
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("*") // Niezbędne ustawienia CORS dla WebSocketów
                .withSockJS(); // Włączenie wsparcia dla SockJS używanego w frontendzie
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Uruchamia prosty broker wiadomości w pamięci (prefix używany w MessageController)
        registry.enableSimpleBroker("/topic");

        // Prefix używany, gdy frontend wysyła wiadomości bezpośrednio do brokera
        registry.setApplicationDestinationPrefixes("/app");
    }
}