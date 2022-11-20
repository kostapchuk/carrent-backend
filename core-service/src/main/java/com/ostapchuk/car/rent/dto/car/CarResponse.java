package com.ostapchuk.car.rent.dto.car;

import java.math.BigDecimal;

public record CarResponse(
        Integer id,
        String mark,
        String model,
        BigDecimal rentPricePerHour,
        BigDecimal bookPricePerHour,
        String imgUrl,
        String carStatus
) {
}
