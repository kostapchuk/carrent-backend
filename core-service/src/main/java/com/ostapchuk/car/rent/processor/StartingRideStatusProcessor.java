package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * This processor is responsible for starting a ride:
 * <br/>1. {@link com.ostapchuk.car.rent.entity.CarStatus#FREE} -> {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT}
 * <br/>2. {@link com.ostapchuk.car.rent.entity.CarStatus#FREE} -> {@link com.ostapchuk.car.rent.entity.CarStatus#IN_BOOKING}
 */
@Component
public class StartingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;
    private final OrderWriteService orderWriteService;

    public StartingRideStatusProcessor(final OrderWriteService orderWriteService,
                                       final OrderReadService orderReadService,
                                       final CarReadService carReadService,
                                       final UserReadService userReadService,
                                       final StatusConverter statusConverter,
                                       final UpdatingRideStatusProcessor updatingRideStatusProcessor) {
        super(orderReadService, carReadService, userReadService, updatingRideStatusProcessor);
        this.statusConverter = statusConverter;
        this.orderWriteService = orderWriteService;
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
        if (orderReadService.existsByUserAndEndingIsNull(user)) {
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
        orderWriteService.save(order);
    }
}
