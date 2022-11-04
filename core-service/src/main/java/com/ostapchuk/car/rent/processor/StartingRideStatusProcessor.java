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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;

@Component
public class StartingRideStatusProcessor extends RideStatusProcessor {

    private final StatusConverter statusConverter;

    public StartingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                       final CarReadService carReadService, final UserReadService userReadService,
                                       final StatusConverter statusConverter) {
        super(orderRepository, orderReadService, carReadService, userReadService);
        this.statusConverter = statusConverter;
    }

    @Override
    public void process(final OrderDto orderDto) {
        if (isStartRide(orderDto)) {
            startRide(orderDto);
        }
    }

    private boolean isStartRide(final OrderDto orderDto) {
        final Car car = carReadService.findById(orderDto.carId());
        return FREE.equals(car.getStatus()) && (IN_BOOKING.equals(orderDto.carStatus())|| IN_RENT.equals(
                orderDto.carStatus()));
    }

    private void startRide(final OrderDto orderDto) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        final Car car = carReadService.findById(orderDto.carId());
        final AtomicBoolean theSameCar = new AtomicBoolean(true);
        orderRepository.findFirstByUserAndEndingIsNull(user).map(Order::getCar).map(Car::getId)
                .ifPresent(id -> theSameCar.set(id.equals(car.getId())));
        if (!theSameCar.get()) {
            throw new OrderCreationException("Could not create another order");
        }
        car.setStatus(orderDto.carStatus());
        createOrder(orderDto, user, car);
    }

    private void createOrder(final OrderDto orderDto, final User user, final Car car) {
        final Order order =
                Order.builder().user(user).uuid(UUID.randomUUID().toString()).start(LocalDateTime.now()).car(car)
                        .status(statusConverter.toOrderStatus(orderDto.carStatus())).build();
        orderRepository.save(order);
    }

}
