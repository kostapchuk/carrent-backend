package com.ostapchuk.car.rent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String secret,
        String header,
        @DurationUnit(SECONDS) Duration accessTokenExpiration,
        @DurationUnit(SECONDS) Duration refreshTokenExpiration
) {
}
