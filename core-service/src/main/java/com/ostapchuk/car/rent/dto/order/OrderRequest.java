package com.ostapchuk.car.rent.dto.order;

import com.ostapchuk.car.rent.entity.CarStatus;

public record OrderRequest(
        Long userId,
        Integer carId,
        CarStatus carStatus
) {
}
