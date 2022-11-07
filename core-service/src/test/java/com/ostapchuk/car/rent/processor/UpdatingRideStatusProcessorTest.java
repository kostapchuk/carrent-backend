package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.converter.StatusConverter;
import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.service.CarReadService;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatingRideStatusProcessorTest {

    StartingRideStatusProcessor startingRideStatusProcessor;

    UpdatingRideStatusProcessor updatingRideStatusProcessor;

    @Mock
    FinishingRideStatusProcessor finishingRideStatusProcessor;

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

    private OrderDto orderDto;
    private final Car car = Car.builder()
            .id(1)
            .mark("BMW")
            .model("M5")
            .imgLink("some-img-link")
            .rentPricePerHour(new BigDecimal("10"))
            .bookPricePerHour(new BigDecimal("8"))
            .status(CarStatus.IN_RENT)
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
    @DisplayName("Car is in rent by the user. The user is active, verified and balance is positive. Should be able to" +
            " pause rent a ride")
    void process_WhenCarIsInRentAndUserIsVerified_ShouldPauseRent() {
        // given
        orderDto = new OrderDto(user.getId(), car.getId(), CarStatus.IN_RENT_PAUSED);
        updatingRideStatusProcessor =
                new UpdatingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, finishingRideStatusProcessor);
        startingRideStatusProcessor =
                new StartingRideStatusProcessor(orderRepository, orderReadService, carReadService, userReadService,
                        statusConverter, updatingRideStatusProcessor);

        // when
        when(carReadService.findStartable(car.getId(), orderDto.carStatus())).thenReturn(Optional.empty());
        when(carReadService.findUpdatable(car.getId(), orderDto.carStatus())).thenReturn(Optional.of(car));
        when(userReadService.findVerifiedById(orderDto.userId())).thenReturn(user);
        when(orderReadService.findExistingOrder(user, car)).thenReturn(new Order());
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // verify
        // todo need to verify that method was called not that other method was not called
        startingRideStatusProcessor.process(orderDto);
        verify(userReadService, times(1)).findVerifiedById(anyLong());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

}
