package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderReadService {

    private final OrderRepository orderRepository;
    private final StatusConverter statusConverter;

    public Order findExistingByUserAndCar(final User user, final Car car) {
        return orderRepository.findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(user, car,
                        statusConverter.toOrderStatus(car.getStatus()))
                .orElseThrow(() -> new EntityNotFoundException("Could not find order"));
    }

    public boolean existsByUserAndEndingIsNull(final User user) {
        return orderRepository.existsByUserAndEndingIsNull(user);
    }
}
