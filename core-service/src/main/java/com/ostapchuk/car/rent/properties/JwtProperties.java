package com.ostapchuk.car.rent.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Getter
@Validated
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    @NotBlank
    private final String secret;
    @NotBlank
    private final String header;
    @NotNull
    @DurationUnit(SECONDS)
    private final Duration accessTokenExpiration;
    @NotNull
    @DurationUnit(SECONDS)
    private final Duration refreshTokenExpiration;
}
