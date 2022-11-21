package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.PriceService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
class UpdatingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;
    private final PriceService priceService;

    public UpdatingRideStatusProcessor(final OrderReadService orderReadService,
                                       final OrderWriteService orderWriteService,
                                       final CarReadService carReadService,
                                       final UserReadService userReadService,
                                       final StatusConverter statusConverter,
                                       final PriceService priceService,
                                       final FinishingRideStatusProcessor finishingRideStatusProcessor) {
        super(orderReadService, carReadService, userReadService, orderWriteService, finishingRideStatusProcessor);
        this.statusConverter = statusConverter;
        this.priceService = priceService;
    }

    @Override
    @Transactional
    public void process(final OrderRequest orderRequest) {
        final Optional<Car> car = carReadService.findUpdatable(orderRequest.carId(), orderRequest.carStatus());
        if (car.isPresent()) {
            updateRide(orderRequest, car.get());
        } else {
            nextProcessor.process(orderRequest);
        }
    }

    private void updateRide(final OrderRequest orderRequest, final Car car) {
        final User user = userReadService.findVerifiedById(orderRequest.userId());
        final String uuid = orderWriteService.finishOrder(orderRequest, car, user);
        final Order newOrder = Order.builder().user(user).uuid(uuid).start(LocalDateTime.now()).car(car)
                .status(statusConverter.toOrderStatus(orderRequest.carStatus())).build();
        orderWriteService.save(newOrder);
    }
}
