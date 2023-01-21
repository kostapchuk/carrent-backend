package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.ride.RideDetailsResponse;
import com.ostapchuk.car.rent.entity.Order;
import org.springframework.stereotype.Component;

@Component
public record OrderMapper() {

    public RideDetailsResponse toRideDetailsResponse(final Order order) {
        return new RideDetailsResponse(
                order.getStart(),
                order.getEnding(),
                order.getStatus().toString(),
                order.getPrice()
        );
    }
}
