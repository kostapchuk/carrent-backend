package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;

@Service
@RequiredArgsConstructor
public class OrderWriteService {

    private final OrderRepository orderRepository;
    private final OrderReadService orderReadService;
    private final CarReadService carReadService;
    private final UserReadService userReadService;
    private final StatusConverter statusConverter;

    private final Random random = new Random(); // TODO

    @Transactional
    public void process(final OrderDto orderDto) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        final Car car = carReadService.findById(orderDto.carId());
        if (isStartRide(orderDto, car)) {
            startRide(orderDto, user, car);
        } else if (isUpdateRide(orderDto, car)) {
            updateStatus(orderDto, user, car);
        } else if (isFinishRide(orderDto, car)) {
            finishRide(orderDto, user, car);
        } else {
            throw new OrderCreationException("Could not create order");
        }
    }

    private boolean isStartRide(final OrderDto orderDto, final Car car) {
        return FREE.equals(car.getStatus()) &&
                (IN_BOOKING.equals(orderDto.carStatus()) || IN_RENT.equals(orderDto.carStatus()));
    }

    private boolean isFinishRide(final OrderDto orderDto, final Car car) {
        return (IN_RENT_PAUSED.equals(car.getStatus()) || IN_RENT.equals(car.getStatus()) ||
                IN_BOOKING.equals(car.getStatus())) && FREE.equals(orderDto.carStatus());
    }

    private boolean isUpdateRide(final OrderDto orderDto, final Car car) {
        return (IN_BOOKING.equals(car.getStatus()) && IN_RENT.equals(orderDto.carStatus())) ||
                (IN_RENT.equals(car.getStatus()) && IN_RENT_PAUSED.equals(orderDto.carStatus())) ||
                (IN_RENT_PAUSED.equals(car.getStatus()) && IN_RENT.equals(orderDto.carStatus()));
    }

    private void updateStatus(final OrderDto orderDto, final User user, final Car car) {
        final Order order = orderReadService.findExistingOrder(user, car);
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L)); // todo
        order.setPrice(orderReadService.calculatePrice(order, car));
        orderRepository.save(order);
        car.setStatus(orderDto.carStatus());
        createOrderUsingExistingOrder(orderDto, user, car, order.getUuid());
    }

    private void createOrderUsingExistingOrder(final OrderDto orderDto, final User user, final Car car,
                                               final String uuid) {
        final Order newOrder = Order.builder()
                .user(user)
                .uuid(uuid)
                .start(LocalDateTime.now())
                .car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus()))
                .build();
        orderRepository.save(newOrder);
    }

    private void finishRide(final OrderDto orderDto, final User user, final Car car) {
        final Order order = orderReadService.findExistingOrder(user, car);
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L));
        order.setPrice(orderReadService.calculatePrice(order, car));
        user.setBalance(user.getBalance().subtract(orderReadService.calculateRidePrice(order, car)));
        car.setStatus(orderDto.carStatus());
        orderRepository.save(order);
    }

    private void startRide(final OrderDto orderDto, final User user, final Car car) {
        final AtomicBoolean theSameCar = new AtomicBoolean(true);
        orderRepository.findFirstByUserAndEndingIsNull(user)
                .map(Order::getCar)
                .map(Car::getId)
                .ifPresent(id -> theSameCar.set(id.equals(car.getId())));
        if (!theSameCar.get()) {
            throw new OrderCreationException("Could not create another order");
        }
        car.setStatus(orderDto.carStatus());
        createOrder(orderDto, user, car);
    }

    private void createOrder(final OrderDto orderDto, final User user, final Car car) {
        final Order order = Order.builder()
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .start(LocalDateTime.now())
                .car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus()))
                .build();
        orderRepository.save(order);
    }
}
