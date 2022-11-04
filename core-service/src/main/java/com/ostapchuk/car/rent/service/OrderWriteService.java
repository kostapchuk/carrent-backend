package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.processor.RideStatusProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService {

    private final List<RideStatusProcessor> rideStatusProcessors;

    public void process(final OrderDto orderDto) {
        rideStatusProcessors.forEach(processor -> processor.process(orderDto));
    }
}
