package com.ostapchuk.car.rent.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {
    @NotBlank
    private final String cloudName;
    @NotNull @Valid
    private final CloudinaryApiProperties api;

    @Getter
    @RequiredArgsConstructor
    public static class CloudinaryApiProperties {
        @NotBlank
        private final String key;
        @NotBlank
        private final String secret;
    }
}
