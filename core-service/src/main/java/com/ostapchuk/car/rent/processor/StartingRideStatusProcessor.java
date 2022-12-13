package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderException;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * This processor is responsible for starting a ride:
 * <br/>1. {@link com.ostapchuk.car.rent.entity.CarStatus#FREE} -> {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT}
 * <br/>2. {@link com.ostapchuk.car.rent.entity.CarStatus#FREE} -> {@link com.ostapchuk.car.rent.entity.CarStatus#IN_BOOKING}
 */
@Service
public class StartingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;


    public StartingRideStatusProcessor(final OrderWriteService orderWriteService,
                                       final OrderReadService orderReadService,
                                       final CarReadService carReadService,
                                       final UserReadService userReadService,
                                       final StatusConverter statusConverter,
                                       final UpdatingRideStatusProcessor updatingRideStatusProcessor) {
        super(orderReadService, carReadService, userReadService, orderWriteService, updatingRideStatusProcessor);
        this.statusConverter = statusConverter;
    }

    @Override
    @Transactional
    public void process(final OrderRequest orderRequest) {
        final Optional<Car> car = carReadService.findStartable(orderRequest.carId(), orderRequest.carStatus());
        if (car.isPresent()) {
            startRide(orderRequest, car.get());
        } else {
            nextProcessor.process(orderRequest);
        }
    }

    private void startRide(final OrderRequest orderRequest, final Car car) {
        final User user = userReadService.findById(orderRequest.userId());
        if (orderReadService.existsByUserAndEndingIsNull(user)) {
            throw new OrderException("Cannot start ride");
        }
        car.setStatus(orderRequest.carStatus());
        final Order order =
                Order.builder()
                        .user(user)
                        .uuid(UUID.randomUUID().toString())
                        .car(car)
                        .status(statusConverter.toOrderStatus(orderRequest.carStatus()))
                        .build();
        orderWriteService.save(order);
    }
}
