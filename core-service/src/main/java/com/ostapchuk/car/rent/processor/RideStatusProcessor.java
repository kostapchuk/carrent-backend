package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RideStatusProcessor {

    final OrderRepository orderRepository;
    final OrderReadService orderReadService;
    final CarReadService carReadService;
    final UserReadService userReadService;

    public abstract void process(OrderDto orderDto);
}
