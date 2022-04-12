package com.ostapchuk.car.rent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "frontend")
public record FrontendLinkConfig(
        @NotNull String link
) {
}
