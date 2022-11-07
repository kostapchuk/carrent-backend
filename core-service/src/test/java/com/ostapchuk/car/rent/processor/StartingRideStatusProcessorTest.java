package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.repository.UserRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartingRideStatusProcessorTest {

    StartingRideStatusProcessor startingRideStatusProcessor;

    @Mock
    UpdatingRideStatusProcessor updatingRideStatusProcessor;

    @Mock
    CarReadService carReadService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderReadService orderReadService;

    @Mock
    StatusConverter statusConverter;

    @Mock
    UserReadService userReadService;

    @Mock
    UserRepository userRepository;

    private OrderDto orderDto;
    private final Car car = Car.builder()
            .id(1)
            .mark("BMW")
            .model("M5")
            .imgLink("some-img-link")
            .rentPricePerHour(new BigDecimal("10"))
            .bookPricePerHour(new BigDecimal("8"))
            .status(CarStatus.FREE)
            .build();

    private final User user = User.builder()
            .id(1L)
            .firstName("FirstName")
            .lastName("LastName")
            .phone("+375332225544")
            .email("user@mailer.com")
            .password("passwordHash1231234")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .verified(true)
            .balance(BigDecimal.ZERO)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();

    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive. Should be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerified_ShouldStart() {
        // given
        orderDto = new OrderDto(user.getId(), car.getId(), CarStatus.IN_BOOKING);
        // todo, replace with autoinject
        startingRideStatusProcessor =
                new StartingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, updatingRideStatusProcessor);

        // when
        when(carReadService.findStartable(car.getId(), orderDto.carStatus())).thenReturn(Optional.of(car));
        when(userReadService.findVerifiedById(orderDto.userId())).thenReturn(user);
        when(orderRepository.existsByUserAndEndingIsNull(user)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        // todo need to verify that method was called not that other method was not called
        startingRideStatusProcessor.process(orderDto);
//        verify(updatingRideStatusProcessor, never()).process(isA(OrderDto.class));
        verify(userReadService, times(1)).findVerifiedById(anyLong());
        verify(orderRepository, times(1)).existsByUserAndEndingIsNull(user);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Car isn't free. User is active, verified and balance is positive. Shouldn't be able to start a ride")
    void process_WhenCarIsNotFreeAndUserIsVerified_ShouldNotStart() {
        // given
        car.setStatus(CarStatus.IN_RENT);
        orderDto = new OrderDto(user.getId(), car.getId(), CarStatus.IN_BOOKING);
        startingRideStatusProcessor =
                new StartingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, updatingRideStatusProcessor);

        // when
        when(carReadService.findStartable(car.getId(), orderDto.carStatus())).thenReturn(Optional.empty());

        // verify
        startingRideStatusProcessor.process(orderDto);
        verify(updatingRideStatusProcessor, times(1)).process(orderDto);
    }

    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive but has an active order. Shouldn't" +
            " be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerifiedAndHasActiveOrder_ShouldNotStart() {
        // given
        orderDto = new OrderDto(user.getId(), car.getId(), CarStatus.IN_BOOKING);
        startingRideStatusProcessor =
                new StartingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, updatingRideStatusProcessor);

        // when
        when(carReadService.findStartable(car.getId(), orderDto.carStatus())).thenReturn(Optional.of(car));
        when(userReadService.findVerifiedById(orderDto.userId())).thenReturn(user);
        when(orderRepository.existsByUserAndEndingIsNull(user)).thenReturn(true);
        final OrderCreationException thrown = assertThrows(
                OrderCreationException.class,
                () -> startingRideStatusProcessor.process(orderDto),
                "Cannot start ride"
        );

        // verify
        assertEquals("Cannot start ride", thrown.getMessage());
        verify(updatingRideStatusProcessor, never()).process(orderDto);
    }

    @Test
    @Disabled("Not implemented yet")
    @DisplayName("Car is free. User is active, NOT verified with positive balance. Shouldn't be able to start a ride")
    void process_WhenCarIsFreeAndUserIsNotVerified_ShouldNotStart() {
        // given
        orderDto = new OrderDto(user.getId() + 10, car.getId(), CarStatus.IN_BOOKING);
        startingRideStatusProcessor =
                new StartingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, updatingRideStatusProcessor);

        // when
        when(carReadService.findStartable(car.getId(), orderDto.carStatus())).thenReturn(Optional.of(car));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        final Long userId = orderDto.userId() + 10;
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userReadService.findVerifiedById(userId),
                "The user with id " + orderDto.userId() + " is not verified yet"
        );

        // verify
        startingRideStatusProcessor.process(orderDto);
        verify(updatingRideStatusProcessor, never()).process(orderDto);
        assertTrue(thrown.getMessage().contains("The user with id"));
        verify(orderRepository, never()).existsByUserAndEndingIsNull(user);
    }

    @Test
    @Disabled("Not implemented yet")
    @DisplayName("Car isn't free. User is active, NOT verified with positive balance. Shouldn't be able to start a " +
            "ride")
    void process_WhenCarIsNotFreeAndUserIsNotVerified_ShouldNotStart() {

    }
}
