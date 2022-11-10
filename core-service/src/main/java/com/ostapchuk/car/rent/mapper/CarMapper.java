package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.car.CarDto;
import com.ostapchuk.car.rent.entity.Car;
import org.springframework.stereotype.Component;

@Component
public record CarMapper() {

    public CarDto toDto(final Car car) {
        return new CarDto(
                car.getId(),
                car.getMark(),
                car.getModel(),
                car.getRentPricePerHour(),
                car.getBookPricePerHour(),
                car.getImgLink(),
                car.getStatus().toString()
        );
    }
}
