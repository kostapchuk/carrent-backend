package com.ostapchuk.car.rent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        @NotNull String secret,
        @NotNull String header,
        @NotNull @DurationUnit(ChronoUnit.MILLIS) Duration expiration,
        @NotNull RefreshJwt refresh
) {
    public record RefreshJwt(@NotNull @DurationUnit(ChronoUnit.MILLIS) Duration expiration) {
    }
}
