package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends CrudRepository<Car, Integer> {

    List<Car> findAllByOrderById();

    List<Car> findAllByStatusOrderById(CarStatus carStatus);
}
