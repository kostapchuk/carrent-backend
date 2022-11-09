package com.ostapchuk.car.rent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudinary")
public record CloudinaryProperties(
        String cloudName,
        CloudinaryApiProperties api
) {

    public record CloudinaryApiProperties(
            String key,
            String secret
    ) {
    }
}
