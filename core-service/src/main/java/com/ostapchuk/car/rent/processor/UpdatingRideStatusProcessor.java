package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
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

import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;

@Component
public class UpdatingRideStatusProcessor extends RideStatusProcessor {

    private final Random random = new Random(); // TODO
    private final StatusConverter statusConverter;

    public UpdatingRideStatusProcessor(final OrderRepository orderRepository, final OrderReadService orderReadService,
                                       final CarReadService carReadService, final UserReadService userReadService,
                                       final StatusConverter statusConverter) {
        super(orderRepository, orderReadService, carReadService, userReadService);
        this.statusConverter = statusConverter;
    }

    @Override
    public void process(final OrderDto orderDto) {
        if (isUpdateRide(orderDto)) {
            updateStatus(orderDto);
        }
    }

    private boolean isUpdateRide(final OrderDto orderDto) {
        final Car car = carReadService.findById(orderDto.carId());
        return (IN_BOOKING.equals(car.getStatus()) && IN_RENT.equals(orderDto.carStatus())) || (IN_RENT.equals(
                car.getStatus()) && IN_RENT_PAUSED.equals(orderDto.carStatus())) || (IN_RENT_PAUSED.equals(
                car.getStatus()) && IN_RENT.equals(orderDto.carStatus()));
    }

    private void updateStatus(final OrderDto orderDto) {
        final User user = userReadService.findVerifiedById(orderDto.userId());
        final Car car = carReadService.findById(orderDto.carId());
        final Order order = orderReadService.findExistingOrder(user, car);
        order.setEnding(LocalDateTime.now().plusHours(random.nextInt(25) + 1L)); // todo
        order.setPrice(orderReadService.calculatePrice(order, car));
        orderRepository.save(order);
        car.setStatus(orderDto.carStatus());
        createOrderUsingExistingOrder(orderDto, user, car, order.getUuid());
    }

    private void createOrderUsingExistingOrder(final OrderDto orderDto, final User user, final Car car,
                                               final String uuid) {
        final Order newOrder = Order.builder().user(user).uuid(uuid).start(LocalDateTime.now()).car(car)
                .status(statusConverter.toOrderStatus(orderDto.carStatus())).build();
        orderRepository.save(newOrder);
    }
}
