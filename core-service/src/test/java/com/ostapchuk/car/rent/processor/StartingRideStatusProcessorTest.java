package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.OrderCreationException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.repository.UserRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link StartingRideStatusProcessor}
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StartingRideStatusProcessor.class, OrderReadService.class, StatusConverter.class})
class StartingRideStatusProcessorTest {

    @Autowired
    private StartingRideStatusProcessor startingRideStatusProcessor;
    @MockBean
    private UpdatingRideStatusProcessor updatingRideStatusProcessor;
    @MockBean
    private CarReadService carReadService;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserReadService userReadService;
    @MockBean
    private UserRepository userRepository;

    @BeforeAll
    protected static void beforeAll() {
        defaultOrderDto = new OrderDto(defaultUser.getId(), defaultCar.getId(), CarStatus.IN_BOOKING);
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderDto)}
     */
    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive. Should be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerified_ShouldStart() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.of(defaultCar));
        when(userReadService.findVerifiedById(defaultOrderDto.userId())).thenReturn(defaultUser);
        when(orderRepository.existsByUserAndEndingIsNull(defaultUser)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        startingRideStatusProcessor.process(defaultOrderDto);
        verify(userReadService, times(1)).findVerifiedById(anyLong());
        verify(orderRepository, times(1)).existsByUserAndEndingIsNull(defaultUser);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderDto)}
     */
    @Test
    @DisplayName("Car isn't free. User is active, verified and balance is positive. Shouldn't be able to start a ride")
    void process_WhenCarIsNotFreeAndUserIsVerified_ShouldNotStart() {
        // given
        defaultCar.setStatus(CarStatus.IN_RENT);

        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.empty());

        // verify
        startingRideStatusProcessor.process(defaultOrderDto);
        verify(updatingRideStatusProcessor, times(1)).process(defaultOrderDto);
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderDto)}
     */
    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive but has an active order. Shouldn't" +
            " be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerifiedAndHasActiveOrder_ShouldNotStart() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderDto.carStatus())).thenReturn(
                Optional.of(defaultCar));
        when(userReadService.findVerifiedById(defaultOrderDto.userId())).thenReturn(defaultUser);
        when(orderRepository.existsByUserAndEndingIsNull(defaultUser)).thenReturn(true);

        // verify
        final OrderCreationException thrown = assertThrows(
                OrderCreationException.class,
                () -> startingRideStatusProcessor.process(defaultOrderDto),
                "Cannot start ride"
        );
        assertEquals("Cannot start ride", thrown.getMessage());
        verify(updatingRideStatusProcessor, never()).process(defaultOrderDto);
    }

    private static OrderDto defaultOrderDto;

    private static final Car defaultCar = Car.builder()
            .id(1)
            .mark("BMW")
            .model("M5")
            .imgLink("some-img-link")
            .rentPricePerHour(new BigDecimal("10"))
            .bookPricePerHour(new BigDecimal("8"))
            .status(CarStatus.FREE)
            .build();

    private static final User defaultUser = User.builder()
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
}
