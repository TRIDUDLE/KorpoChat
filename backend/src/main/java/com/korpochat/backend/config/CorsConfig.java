package com.korpochat.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Zastosuj do wszystkich endpointów, m.in. /api/auth i /api/users
                .allowedOriginPatterns("*") // Pozwala na zapytania z dowolnego adresu (w środowisku produkcyjnym warto zmienić to na konkretny adres URL frontendu)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Dozwolone metody HTTP
                .allowedHeaders("*")
                .allowCredentials(true); // Wymagane, jeśli planujesz używać ciasteczek lub uwierzytelniania
    }
}