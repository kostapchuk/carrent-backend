package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.ride.RideDetailsDto;
import com.ostapchuk.car.rent.dto.ride.RideDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.Constant;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideService {

    private final UserReadService userReadService;
    private final OrderRepository orderRepository;
    private final PriceService priceService;

    public List<RideDto> findAllRidesByUserId(final Long id) {
        final User user = userReadService.findById(id);
        final Map<String, List<Order>> rides = orderRepository.findAllByUserAndEndingIsNotNullOrderByStartAsc(user)
                .stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        return processRides(rides);
    }

    private List<RideDto> processRides(final Map<String, List<Order>> rides) {
        final List<RideDto> ridesDto = new ArrayList<>();
        for (final Map.Entry<String, List<Order>> entry : rides.entrySet()) {
            final List<RideDetailsDto> rideDetailsDtos = new ArrayList<>();
            entry.getValue()
                    .forEach(order -> rideDetailsDtos.add(new RideDetailsDto(order.getStart(), order.getEnding(),
                            order.getStatus().toString(), order.getPrice())));
            final Order order = entry.getValue()
                    .get(Constant.ZERO_INT);
            final Car car = order.getCar();
            ridesDto.add(new RideDto(order.getStart().toLocalDate(), car.getMark(), car.getModel(),
                    priceService.calculateRidePriceByOrders(entry.getValue()),
                    retrieveRideTimeByOrders(entry.getValue()), rideDetailsDtos));
        }
        return ridesDto.stream()
                .sorted(Comparator.comparing(RideDto::date).reversed())
                .toList();
    }

    private int retrieveRideTimeByOrders(final List<Order> orders) {
        return orders.stream()
                .map(o -> DateTimeUtil.retrieveDurationInHours(o.getStart(), o.getEnding()))
                .reduce(Constant.ZERO_INT, Integer::sum);
    }
}
