package com.ostapchuk.car.rent;

import com.ostapchuk.car.rent.properties.CloudinaryProperties;
import com.ostapchuk.car.rent.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, CloudinaryProperties.class})
public class CarrentBackendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CarrentBackendApplication.class, args);
    }
}
