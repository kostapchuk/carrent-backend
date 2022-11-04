package com.ostapchuk.car.rent.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private static final Map CONFIG = ObjectUtils.asMap(
            );

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(CONFIG);
    }
}
