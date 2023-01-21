package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.ride.RideDetailsResponse;
import com.ostapchuk.car.rent.dto.ride.RideResponse;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.OrderMapper;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.Constant;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideReadService {

    private final OrderMapper orderMapper;
    private final PriceService priceService;
    private final UserReadService userReadService;
    private final OrderRepository orderRepository;

    public List<RideResponse> findAllRidesByUserId(final Long id) {
        final User user = userReadService.findById(id);
        final Map<String, List<Order>> rides = orderRepository.findAllByUserAndEndingIsNotNullOrderByStartAsc(user)
                .stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        return retrieveRideResponses(rides);
    }

    private List<RideResponse> retrieveRideResponses(final Map<String, List<Order>> rides) {
        return rides.entrySet().stream()
                .map(this::retrieveRideResponse)
                .sorted(Comparator.comparing(RideResponse::date).reversed())
                .toList();
    }

    private RideResponse retrieveRideResponse(final Map.Entry<String, List<Order>> entry) {
        final Order order = entry.getValue().stream()
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("No orders found for the " +
                        "corresponding uuid of orders: " + entry.getKey()));
        return new RideResponse(
                order.getStart().toLocalDate(),
                order.getCar().getMark(),
                order.getCar().getModel(),
                priceService.calculateRidePrice(order.getUuid()),
                retrieveRideTime(entry.getValue()),
                retrieveRideDetailsResponses(entry)
        );
    }

    private int retrieveRideTime(final List<Order> orders) {
        return orders.stream()
                .map(o -> DateTimeUtil.retrieveDurationInMinutes(o.getStart(), o.getEnding()))
                .reduce(Constant.ZERO_INT, Integer::sum);
    }

    private List<RideDetailsResponse> retrieveRideDetailsResponses(final Map.Entry<String, List<Order>> entry) {
        return entry.getValue().stream().map(orderMapper::toRideDetailsResponse).toList();
    }
}
