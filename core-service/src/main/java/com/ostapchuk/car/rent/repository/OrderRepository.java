package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findFirstByUserAndEndingIsNull(User user);

    List<Order> findAllByUuid(String uuid);

    Optional<Order> findFirstByUserAndCarAndEndingIsNullAndStatusOrderByStartDesc(User user, Car car,
                                                                                  OrderStatus status);

    List<Order> findAllByUserAndEndingIsNotNullOrderByStartAsc(User user);
}
