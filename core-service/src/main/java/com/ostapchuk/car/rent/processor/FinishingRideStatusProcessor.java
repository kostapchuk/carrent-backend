package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.exception.CarUnavailableException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@org.springframework.core.annotation.Order(3)
class FinishingRideStatusProcessor extends RideStatusProcessor {

    public FinishingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                        final CarReadService carReadService, final UserReadService userReadService) {
        super(orderRepository, orderReadService, carReadService, userReadService, null);
    }

    @Override
    public void process(final OrderDto orderDto) {
        final Optional<Car> car = carReadService.findFinishable(orderDto.carId(), orderDto.carStatus());
        if (car.isPresent()) {
            finishRide(orderDto, car.get());
        } else {
            throw new CarUnavailableException("Sorry, car is not available");
        }
    }

    private void finishRide(final OrderDto orderDto, final Car car) {
        orderReadService.complete(orderDto, car);
    }
}
