package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.OrderWriteService;
import com.ostapchuk.car.rent.service.PriceService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link UpdatingRideStatusProcessor}
 */
@SpringJUnitConfig(classes = {
        FinishingRideStatusProcessor.class, StatusConverter.class,
        UpdatingRideStatusProcessor.class, StartingRideStatusProcessor.class
})
class UpdatingRideStatusProcessorTest {

    @Autowired
    private StartingRideStatusProcessor startingRideStatusProcessor;
    @MockBean
    private CarReadService carReadService;
    @MockBean
    private OrderWriteService orderWriteService;
    @MockBean
    private OrderReadService orderReadService;
    @MockBean
    private UserReadService userReadService;
    @MockBean
    private PriceService priceService;

    @BeforeAll
    protected static void beforeAll() {
        defaultOrderRequest = new OrderRequest(defaultUser.getId(), defaultCar.getId(), CarStatus.IN_BOOKING);
    }

    /**
     * {@link UpdatingRideStatusProcessor#process(OrderRequest)}
     */
    @Test
    @DisplayName("Car is in rent by the user. The user is active, verified and balance is positive. Should be able to" +
            " pause rent a ride")
    void process_WhenCarIsInRentAndUserIsVerified_ShouldPauseRent() {
        // when
        when(carReadService.findStartable(defaultCar.getId(), defaultOrderRequest.carStatus())).thenReturn(
                Optional.empty());
        when(carReadService.findUpdatable(defaultCar.getId(), defaultOrderRequest.carStatus())).thenReturn(Optional.of(
                defaultCar));
        when(userReadService.findVerifiedById(defaultOrderRequest.userId())).thenReturn(defaultUser);
        when(orderReadService.findExistingByUserAndCar(defaultUser, defaultCar)).thenReturn(new Order());
        when(orderWriteService.save(any(Order.class))).thenReturn(new Order());

        // verify
        startingRideStatusProcessor.process(defaultOrderRequest);
        verify(userReadService, times(1)).findVerifiedById(anyLong());
        verify(orderWriteService, times(2)).save(any(Order.class));
    }

    private static OrderRequest defaultOrderRequest;
    private static final Car defaultCar = Car.builder()
            .id(1)
            .mark("BMW")
            .model("M5")
            .imgLink("some-img-link")
            .rentPricePerHour(new BigDecimal("10"))
            .bookPricePerHour(new BigDecimal("8"))
            .status(CarStatus.IN_RENT)
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
