package com.ostapchuk.car.rent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        @NotBlank String cloudName,
        @NotNull @Valid CloudinaryApiProperties api
) {
    public record CloudinaryApiProperties(
            @NotBlank String key,
            @NotBlank String secret
    ) {
    }
}
