package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;

/**
 * The processor has implementations {@link StartingRideStatusProcessor}, {@link UpdatingRideStatusProcessor},
 * {@link FinishingRideStatusProcessor} that help changing the status of a car and creating orders to charge the user
 * after a ride.
 */
@RequiredArgsConstructor
public abstract class RideStatusProcessor {

    final OrderReadService orderReadService;
    final CarReadService carReadService;
    final UserReadService userReadService;
    final OrderWriteService orderWriteService;
    final RideStatusProcessor nextProcessor;

    public abstract void process(OrderDto orderDto);
}
