package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@org.springframework.core.annotation.Order(1)
public class StartingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;

    public StartingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                       final CarReadService carReadService, final UserReadService userReadService,
                                       final StatusConverter statusConverter,
                                       final UpdatingRideStatusProcessor updatingRideStatusProcessor) {
        super(orderRepository, orderReadService, carReadService, userReadService, updatingRideStatusProcessor);
        this.statusConverter = statusConverter;
    }

    @Override
    public void process(final OrderDto orderDto) {
        final Optional<Car> car = carReadService.findStartable(orderDto.carId(), orderDto.carStatus());
        if (car.isPresent()) {
            startRide(orderDto, car.get());
        } else {
            nextProcessor.process(orderDto);
        }
    }

    private void startRide(final OrderDto orderDto, final Car car) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        if (orderRepository.existsByUserAndEndingIsNull(user)) {
            throw new OrderCreationException("Cannot start ride");
        }
        car.setStatus(orderDto.carStatus());
        final Order order =
                Order.builder()
                        .user(user)
                        .uuid(UUID.randomUUID().toString())
                        .start(LocalDateTime.now())
                        .car(car)
                        .status(statusConverter.toOrderStatus(orderDto.carStatus()))
                        .build();
        orderRepository.save(order);
    }
}
