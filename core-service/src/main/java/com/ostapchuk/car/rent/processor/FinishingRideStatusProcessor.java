package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;

@Component
public class FinishingRideStatusProcessor extends RideStatusProcessor {

    private final Random random = new Random(); // TODO

    public FinishingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                        final CarReadService carReadService, final UserReadService userReadService) {
        super(orderRepository, orderReadService, carReadService, userReadService);
    }

    @Override
    public void process(final OrderDto orderDto) {
        if (isFinishRide(orderDto)) {
            finishRide(orderDto);
        }
    }

    private boolean isFinishRide(final OrderDto orderDto) {
        final Car car = carReadService.findById(orderDto.carId());
        return (IN_RENT_PAUSED.equals(car.getStatus()) || IN_RENT.equals(car.getStatus()) ||
                IN_BOOKING.equals(car.getStatus())) && FREE.equals(orderDto.carStatus());
    }


    private void finishRide(final OrderDto orderDto) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        final Car car = carReadService.findById(orderDto.carId());
        final Order order = orderReadService.findExistingOrder(user, car);
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L)); // todo
        order.setPrice(orderReadService.calculatePrice(order, car));
        user.setBalance(user.getBalance().subtract(orderReadService.calculateRidePrice(order, car)));
        car.setStatus(orderDto.carStatus());
        orderRepository.save(order);
    }

}
