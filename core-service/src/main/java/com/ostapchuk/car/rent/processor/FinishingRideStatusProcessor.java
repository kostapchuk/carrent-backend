package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.CarUnavailableException;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.PriceService;
import com.ostapchuk.car.rent.service.UserReadService;
import com.ostapchuk.car.rent.service.UserWriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * This processor is responsible for finishing a ride:
 * <br/>1. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_BOOKING} ->
 * {@link com.ostapchuk.car.rent.entity.CarStatus#FREE}
 * <br/>2. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT} ->
 * {@link com.ostapchuk.car.rent.entity.CarStatus#FREE}
 * <br/>3. {@link com.ostapchuk.car.rent.entity.CarStatus#IN_RENT_PAUSED} ->
 * {@link com.ostapchuk.car.rent.entity.CarStatus#FREE}
 */
@Service
class FinishingRideStatusProcessor extends RideStatusProcessor {

    private final PriceService priceService;
    private final UserWriteService userWriteService;

    public FinishingRideStatusProcessor(final OrderReadService orderReadService,
                                        final CarReadService carReadService,
                                        final OrderWriteService orderWriteService,
                                        final PriceService priceService,
                                        final UserReadService userReadService,
                                        final UserWriteService userWriteService) {
        super(orderReadService, carReadService, userReadService, orderWriteService, null);
        this.priceService = priceService;
        this.userWriteService = userWriteService;
    }

    @Override
    @Transactional
    public void process(final OrderRequest orderRequest) {
        final Optional<Car> car = carReadService.findFinishable(orderRequest.carId(), orderRequest.carStatus());
        if (car.isPresent()) {
            finishRide(orderRequest, car.get());
        } else {
            throw new CarUnavailableException("Sorry, car is not available");
        }
    }

    private void finishRide(final OrderRequest orderRequest, final Car car) {
        final User user = userReadService.findVerifiedById(orderRequest.userId());
        final String uuid = orderWriteService.finishOrder(orderRequest, car, user);
        user.setBalance(user.getBalance().subtract(priceService.calculateRidePrice(uuid)));
        userWriteService.save(user);
    }
}
