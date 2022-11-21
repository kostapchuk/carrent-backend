package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CarRepository extends CrudRepository<Car, Integer> {

    List<Car> findAllByStatusOrderById(CarStatus carStatus);

    Optional<Car> findByIdAndStatus(Integer id, CarStatus carStatus);

    Optional<Car> findByIdAndStatusIn(Integer id, Set<CarStatus> statuses);
}
