package com.ostapchuk.car.rent.config;

import com.cloudinary.Cloudinary;
import com.ostapchuk.car.rent.properties.CloudinaryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final CloudinaryProperties cloudinaryProperties;
    private Map<String, String> config;

    @PostConstruct
    protected void init() {
        config = Map.of(
                "cloud_name", cloudinaryProperties.getCloudName(),
                "api_key", cloudinaryProperties.getApi().getKey(),
                "api_secret", cloudinaryProperties.getApi().getSecret()
        );
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(config);
    }
}
