package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.repository.CarRepository;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService {

    private final OrderRepository orderRepository;
    private final OrderReadService orderReadService;
    private final PriceService priceService;
    private final CarRepository carRepository;

    public Order save(final Order order) {
        return orderRepository.save(order);
    }

    public String finishOrder(final OrderRequest orderRequest, final Car car, final User user) {
        final Order order = orderReadService.findExistingByUserAndCar(user, car);
        order.setEnding(LocalDateTime.now());
        order.setPrice(priceService.calculatePrice(order));
        orderRepository.save(order);
        car.setStatus(orderRequest.carStatus());
        carRepository.save(car);
        return order.getUuid();
    }
}
