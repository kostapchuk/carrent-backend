package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderException;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
@SpringJUnitConfig(classes = {StartingRideStatusProcessor.class, StatusConverter.class})
class StartingRideStatusProcessorTest {

    @Autowired
    private StartingRideStatusProcessor startingRideStatusProcessor;
    @MockBean
    private UpdatingRideStatusProcessor updatingRideStatusProcessor;
    @MockBean
    private CarReadService carReadService;
    @MockBean
    private UserReadService userReadService;
    @MockBean
    private OrderWriteService orderWriteService;
    @MockBean
    private OrderReadService orderReadService;

    @BeforeAll
    protected static void beforeAll() {
        defaultOrderRequest = new OrderRequest(defaultUser.getId(), defaultCar.getId(), CarStatus.IN_BOOKING);
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderRequest)}
     */
    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive. Should be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerified_ShouldStart() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderRequest.carStatus())).thenReturn(
                Optional.of(defaultCar));
        when(userReadService.findById(defaultOrderRequest.userId())).thenReturn(defaultUser);
        when(orderReadService.existsByUserAndEndingIsNull(defaultUser)).thenReturn(false);
        when(orderWriteService.save(any(Order.class))).thenReturn(new Order());

        // verify
        startingRideStatusProcessor.process(defaultOrderRequest);
        verify(userReadService, times(1)).findById(anyLong());
        verify(orderReadService, times(1)).existsByUserAndEndingIsNull(defaultUser);
        verify(orderWriteService, times(1)).save(any(Order.class));
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderRequest)}
     */
    @Test
    @DisplayName("Car isn't free. User is active, verified and balance is positive. Shouldn't be able to start a ride")
    void process_WhenCarIsNotFreeAndUserIsVerified_ShouldNotStart() {
        // given
        defaultCar.setStatus(CarStatus.IN_RENT);

        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderRequest.carStatus())).thenReturn(
                Optional.empty());

        // verify
        startingRideStatusProcessor.process(defaultOrderRequest);
        verify(updatingRideStatusProcessor, times(1)).process(defaultOrderRequest);
    }

    /**
     * {@link StartingRideStatusProcessor#process(OrderRequest)}
     */
    @Test
    @DisplayName("Car is free. User is active, verified and balance is positive but has an active order. Shouldn't" +
            " be able to start a ride")
    void process_WhenCarIsFreeAndUserIsVerifiedAndHasActiveOrder_ShouldNotStart() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderRequest.carStatus())).thenReturn(
                Optional.of(defaultCar));
        when(userReadService.findById(defaultOrderRequest.userId())).thenReturn(defaultUser);
        when(orderReadService.existsByUserAndEndingIsNull(defaultUser)).thenReturn(true);

        // verify
        final OrderException thrown = assertThrows(
                OrderException.class,
                () -> startingRideStatusProcessor.process(defaultOrderRequest)
        );
        assertEquals("Cannot start ride", thrown.getMessage());
        verify(updatingRideStatusProcessor, never()).process(defaultOrderRequest);
    }

    private static OrderRequest defaultOrderRequest;

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
            .verified(true)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();
}
