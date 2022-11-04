package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

@Component
public class FinishingRideStatusProcessor extends RideStatusProcessor {

    public FinishingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                        final CarReadService carReadService, final UserReadService userReadService) {
        super(orderRepository, orderReadService, carReadService, userReadService);
    }

    @Override
    public void process(final OrderDto orderDto) {
        final Car car = carReadService.findFinishable(orderDto.carId(), orderDto.carStatus());
        finishRide(orderDto, car);
    }

    private void finishRide(final OrderDto orderDto, final Car car) {
        orderReadService.complete(orderDto, car);
    }
}
