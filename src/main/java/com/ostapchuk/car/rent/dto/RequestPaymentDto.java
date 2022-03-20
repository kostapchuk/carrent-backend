package com.ostapchuk.car.rent.dto;

import java.math.BigDecimal;

public record RequestPaymentDto(
        BigDecimal price
) {
}
