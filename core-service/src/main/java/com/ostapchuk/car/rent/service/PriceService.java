package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.exception.OrderException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final OrderRepository orderRepository;

    public BigDecimal calculatePrice(final Order order) {
        final long minutes = DateTimeUtil.retrieveDurationInMinutes(order.getStart(), order.getEnding());
        final BigDecimal minutePrice = switch (order.getStatus()) {
            case BOOKING -> order.getCar().getBookPricePerHour();
            case RENT, RENT_PAUSED -> order.getCar().getRentPricePerHour();
        };
        return minutePrice.multiply(new BigDecimal(minutes));
    }

    public BigDecimal calculateRidePrice(final String uuid) {
        final List<Order> orders = orderRepository.findAllByUuid(uuid);
        if (orders.stream().map(Order::getEnding).anyMatch(Objects::isNull)) {
            throw new OrderException("Can not calculate price for not finished order");
        }
        return retrieveRidePrice(orders);
    }

    private BigDecimal retrieveRidePrice(final List<Order> orders) {
        return orders.stream()
                .map(Order::getPrice)
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);
    }
}
