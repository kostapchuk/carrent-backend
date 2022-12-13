package com.ostapchuk.car.rent.dto.ride;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RideResponse(
        LocalDate date,
        String mark,
        String model,
        BigDecimal totalPrice,
        int totalTime,
        List<RideDetailsResponse> rideDetailsResponses
) {
}
