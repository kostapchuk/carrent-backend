package com.ostapchuk.car.rent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotBlank String header,
        @NotNull @DurationUnit(SECONDS) Duration accessTokenExpiration,
        @NotNull @DurationUnit(SECONDS) Duration refreshTokenExpiration
) {
}
