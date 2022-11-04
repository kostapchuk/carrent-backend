package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.ride.RideDetailsDto;
import com.ostapchuk.car.rent.dto.ride.RideDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.Constant;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT_PAUSED;
import static java.math.BigDecimal.ZERO;

@Service
public record OrderReadService(
        OrderRepository orderRepository,
        UserReadService userReadService,
        StatusConverter statusConverter
) {

    public List<RideDto> findAllRidesByUserId(final Long id) {
        final User user = userReadService.findById(id);
        final Map<String, List<Order>> rides = orderRepository.findAllByUserAndEndingIsNotNullOrderByStartAsc(user)
                .stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        return processRides(rides);
    }

    Order findExistingOrder(final User user, final Car car) {
        return orderRepository.findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(user, car,
                        statusConverter.toOrderStatus(car.getStatus()))
                .orElseThrow(() -> new EntityNotFoundException("Could not find order"));
    }

    BigDecimal calculatePrice(final Order order, final Car car) {
        final long hours = DateTimeUtil.retrieveDurationInHours(order.getStart(), order.getEnding());
        final BigDecimal hourPrice = switch (order.getStatus()) {
            case BOOKING -> car.getBookPricePerHour();
            case RENT, RENT_PAUSED -> car.getRentPricePerHour();
        };
        return hourPrice.multiply(new BigDecimal(hours));
    }

    BigDecimal calculateRidePrice(final Order order, final Car car) {
        final BigDecimal price = calculatePrice(order, car);
        if (RENT.equals(order.getStatus()) || RENT_PAUSED.equals(order.getStatus())) {
            final List<Order> orders = orderRepository.findAllByUuid(order.getUuid());
            return retrieveRidePriceByOrders(orders).add(price);
        } else {
            return price;
        }
    }

    private List<RideDto> processRides(final Map<String, List<Order>> rides) {
        final List<RideDto> ridesDto = new ArrayList<>();
        for (final Map.Entry<String, List<Order>> entry : rides.entrySet()) {
            final List<RideDetailsDto> rideDetailsDtos = new ArrayList<>();
            entry.getValue()
                    .forEach(order -> rideDetailsDtos.add(new RideDetailsDto(order.getStart(), order.getEnding(),
                            order.getStatus()
                                    .toString(), order.getPrice())));
            final Order order = entry.getValue()
                    .get(Constant.ZERO_INT);
            final Car car = order.getCar();
            ridesDto.add(new RideDto(order.getStart()
                    .toLocalDate(), car.getMark(), car.getModel(),
                    retrieveRidePriceByOrders(entry.getValue()), retrieveRideTimeByOrders(entry.getValue()),
                    rideDetailsDtos));
        }
        return ridesDto.stream()
                .sorted(Comparator.comparing(RideDto::date)
                        .reversed())
                .toList();
    }

    private int retrieveRideTimeByOrders(final List<Order> orders) {
        return orders.stream()
                .map(o -> DateTimeUtil.retrieveDurationInHours(o.getStart(), o.getEnding()))
                .reduce(Constant.ZERO_INT, Integer::sum);
    }

    private BigDecimal retrieveRidePriceByOrders(final List<Order> orders) {
        return orders.stream()
                .map(Order::getPrice)
                .reduce(ZERO, BigDecimal::add);
    }
}
