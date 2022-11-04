package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.car.CarDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.exception.CarUnavailableException;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.CarMapper;
import com.ostapchuk.car.rent.repository.CarRepository;
import com.ostapchuk.car.rent.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;

@Service
public record CarReadService(
        CarRepository carRepository,
        OrderRepository orderRepository,
        UserReadService userReadService,
        CarMapper carMapper
) {
    public List<CarDto> findAll() {
        return carRepository.findAllByOrderById()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public CarDto findDtoById(final Integer id) {
        return carMapper.toDto(findById(id));
    }

    public Car findById(final Integer id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id));
    }

    public List<CarDto> findAllFree() {
        return carRepository.findAllByStatusOrderById(FREE)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public Car findStartable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> FREE.equals(car.getStatus()) &&
                        (IN_BOOKING.equals(carStatus) || IN_RENT.equals(carStatus))
                )
                .orElseThrow(() -> new CarUnavailableException("Sorry, the car is unavailable"));
    }

    public Car findUpdatable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> (IN_BOOKING.equals(car.getStatus()) && IN_RENT.equals(carStatus)) || (IN_RENT.equals(
                        car.getStatus()) && IN_RENT_PAUSED.equals(carStatus)) || (IN_RENT_PAUSED.equals(
                        car.getStatus()) && IN_RENT.equals(carStatus))
                )
                .orElseThrow(() -> new CarUnavailableException("Sorry, the car is unavailable"));
    }

    public Car findFinishable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> FREE.equals(carStatus) &&
                        (IN_RENT_PAUSED.equals(car.getStatus()) || IN_RENT.equals(car.getStatus()) ||
                        IN_BOOKING.equals(car.getStatus()))
                )
                .orElseThrow(() -> new CarUnavailableException("Sorry, the car is unavailable"));
    }

    public List<CarDto> findAllFreeForUser(final Long userId) {
        final List<Car> freeCars = new ArrayList<>();
        orderRepository.findFirstByUserAndEndingIsNull(userReadService.findById(userId))
                .map(Order::getCar)
                .ifPresent(freeCars::add);
        freeCars.addAll(carRepository.findAllByStatusOrderById(FREE));
        return freeCars.stream()
                .map(carMapper::toDto)
                .toList();
    }
}
