package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.car.CarResponse;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.CarMapper;
import com.ostapchuk.car.rent.repository.CarRepository;
import com.ostapchuk.car.rent.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarReadService {

    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final UserReadService userReadService;
    private final CarMapper carMapper;

    public CarResponse findById(final Integer id) {
        return carMapper.toDto(
                carRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id))
        );
    }

    public List<CarResponse> findAllFree() {
        return carRepository.findAllByStatusOrderById(FREE)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    public Optional<Car> findStartable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> FREE.equals(car.getStatus()) &&
                        (IN_BOOKING.equals(carStatus) || IN_RENT.equals(carStatus))
                );
    }

    public Optional<Car> findUpdatable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> (IN_BOOKING.equals(car.getStatus()) && IN_RENT.equals(carStatus)) || (IN_RENT.equals(
                        car.getStatus()) && IN_RENT_PAUSED.equals(carStatus)) || (IN_RENT_PAUSED.equals(
                        car.getStatus()) && IN_RENT.equals(carStatus))
                );
    }

    public Optional<Car> findFinishable(final Integer carId, final CarStatus carStatus) {
        return carRepository.findById(carId)
                .filter(car -> FREE.equals(carStatus) &&
                        (IN_RENT_PAUSED.equals(car.getStatus()) || IN_RENT.equals(car.getStatus()) ||
                                IN_BOOKING.equals(car.getStatus()))
                );
    }

    public List<CarResponse> findAllFreeForUser(final Long userId) {
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
