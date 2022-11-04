package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findFirstByPersonAndEndingIsNull(Person person);

    boolean existsByPersonAndEndingIsNull(Person person);

    List<Order> findAllByUuid(String uuid);

    Optional<Order> findFirstByPersonAndCarAndEndingIsNullAndStatusOrderByStartDesc(Person person, Car car,
                                                                                    OrderStatus status);

    List<Order> findAllByPersonAndEndingIsNotNullOrderByStartAsc(Person person);
}
