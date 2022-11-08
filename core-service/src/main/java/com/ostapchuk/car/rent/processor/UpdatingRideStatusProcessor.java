package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This processor is responsible for updating a ride:
 * <br/>1. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_BOOKING} -> {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT}
 * <br/>2. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT} ->
 * {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT_PAUSED}
 * <br/>3. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT_PAUSED} ->
 * {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT}
 */
@Component
class UpdatingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;
    private final OrderWriteService orderWriteService;

    public UpdatingRideStatusProcessor(final OrderWriteService orderWriteService,
                                       final OrderReadService orderReadService,
                                       final CarReadService carReadService,
                                       final UserReadService userReadService,
                                       final StatusConverter statusConverter,
                                       final FinishingRideStatusProcessor finishingRideStatusProcessor) {
        super(orderReadService, carReadService, userReadService, finishingRideStatusProcessor);
        this.statusConverter = statusConverter;
        this.orderWriteService = orderWriteService;
    }

    @Override
    public void process(final OrderDto orderDto) {
        final Optional<Car> car = carReadService.findUpdatable(orderDto.carId(), orderDto.carStatus());
        if (car.isPresent()) {
            updateRide(orderDto, car.get());
        } else {
            nextProcessor.process(orderDto);
        }
    }

    private void updateRide(final OrderDto orderDto, final Car car) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        final Order order = orderReadService.findExistingOrder(user, car);
        order.setEnding(LocalDateTime.now());
        order.setPrice(orderReadService.calculatePrice(order, car));
        orderWriteService.save(order);
        car.setStatus(orderDto.carStatus());
        final Order newOrder = Order.builder().user(user).uuid(order.getUuid()).start(LocalDateTime.now()).car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus())).build();
        orderWriteService.save(newOrder);
    }
}
