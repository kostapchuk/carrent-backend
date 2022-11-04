package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.processor.StartingRideStatusProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService {

    private final StartingRideStatusProcessor startingRideStatusProcessor;

    public void process(final OrderDto orderDto) {
        startingRideStatusProcessor.process(orderDto);
    }
}
