package com.ostapchuk.car.rent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class ApplicationConfig {

    @Bean
    public Map<HttpMethod, String[]> publicEndpoints() {
        return Map.of(
                POST, new String[]{"/api/v1/auth/login", "/api/v1/users"},
                GET, new String[]{
                        "/api/v1/cars", "/api/v1/cars/*", "/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html", "/actuator/**"
                }
        );
    }
}
