package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.NegativeBalanceException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private CarService carService;

    @MockBean
    private UserService userService;

    @Autowired
    private OrderService orderService;

    // TODO: 3/17/2022 Test cases
    // 1. Book Free car
    // 2. Book Rent car
    // 3. Book Second car
    // 4. Book -> Rent car
    // 5. Rent -> Rent Paused car

    @DisplayName("Book Free car")
    @Test
    void bookFreeCar_UserValidAndBalanceNotNegativeAndCarIsFreeAndUserNotHaveOtherOrders() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, BigDecimal.ZERO, true);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.FREE);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        orderService.process(orderDto);
    }

    @DisplayName("Book the car when user is valid, balance not negative, car is in rent and user does not have active orders")
    @Test
    void bookInRentCar_UserValidAndBalanceNotNegativeAndCarIsRentAndUserNotHaveOtherOrders() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, BigDecimal.ZERO, true);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.IN_RENT);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        assertThrows(CarUnavailableException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }

    @DisplayName("Book the car when user is valid, balance not negative, car is in booking and user does not have active orders")
    @Test
    void bookInBookingCar_UserValidAndBalanceNotNegativeAndCarIsRentAndUserNotHaveOtherOrders() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, BigDecimal.ZERO, true);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.IN_BOOKING);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        assertThrows(CarUnavailableException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }

    @DisplayName("Book the car when user is valid, balance not negative, car is unavailable and user does not have active orders")
    @Test
    void bookUnavailableCar_UserValidAndBalanceNotNegativeAndCarUnavailableAndUserNotHaveOtherOrders() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, BigDecimal.ZERO, true);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.UNAVAILABLE);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        assertThrows(CarUnavailableException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }

    @DisplayName("Book the car when user is valid, balance is negative, car is free and user does not have active orders")
    @Test
    void bookCar_1() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, new BigDecimal("-10.0"), true);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.FREE);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        assertThrows(NegativeBalanceException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }

    @DisplayName("Book the car " +
            "when user is not valid, " +
            "balance not negative, " +
            "car is free and " +
            "user does not have active orders")
    @Test
    void bookCar_2() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 1, CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, new BigDecimal("10.0"), false);
        final Car car = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.FREE);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(car);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
//        verify(orderRepository, times(0)).findFirstByUserAndEndingNotNull(user);
        assertThrows(UserUnverifiedException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }

    @DisplayName("Book the car " +
            "when user is valid, " +
            "balance not negative, " +
            "car is free and " +
            "user have active bookings")
    @Test
    void bookCar_3() {
        // setup
        final OrderDto orderDto = new OrderDto(1L, 2, CarStatus.IN_RENT);
        final Car bookedCar = new Car(1, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.IN_BOOKING);
        final User user = new User(1L, "", "", "", "", "", Role.USER, new BigDecimal("10.0"), true);
        final Order order = new Order(2L, bookedCar, user, LocalDateTime.now(), null, null);
        final Car wantToBook = new Car(2, "", "", "", new BigDecimal("2.2"), new BigDecimal("3.3"), CarStatus.FREE);

        // when
        when(userService.findById(1L)).thenReturn(user);
        when(carService.findById(1)).thenReturn(bookedCar);
        when(carService.findById(2)).thenReturn(wantToBook);
        when(orderRepository.findFirstByUserAndEndingIsNull(user)).thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        assertThrows(CarUnavailableException.class, () -> orderService.process(orderDto),
                "The expected exception was not thrown");
//        assertTrue(thrown.getMessage().contains("unavailable"));
    }
}