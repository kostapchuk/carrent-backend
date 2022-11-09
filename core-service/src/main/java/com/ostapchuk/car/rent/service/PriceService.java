package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT_PAUSED;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final OrderRepository orderRepository;

    public BigDecimal calculatePrice(final Order order, final Car car) {
        final long hours = DateTimeUtil.retrieveDurationInHours(order.getStart(), order.getEnding());
        final BigDecimal hourPrice = switch (order.getStatus()) {
            case BOOKING -> car.getBookPricePerHour();
            case RENT, RENT_PAUSED -> car.getRentPricePerHour();
        };
        return hourPrice.multiply(new BigDecimal(hours));
    }

    public BigDecimal calculateRidePrice(final Order order, final Car car) {
        final BigDecimal price = calculatePrice(order, car);
        if (RENT.equals(order.getStatus()) || RENT_PAUSED.equals(order.getStatus())) {
            final List<Order> orders = orderRepository.findAllByUuid(order.getUuid());
            return calculateRidePriceByOrders(orders).add(price);
        } else {
            return price;
        }
    }

    BigDecimal calculateRidePriceByOrders(final List<Order> orders) {
        return orders.stream()
                .map(Order::getPrice)
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);
    }
}
