package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.OrderException;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.OrderStatus.RENT;
import static java.time.Month.NOVEMBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = PriceService.class)
class PriceServiceTest {

    @Autowired
    private PriceService priceService;

    @MockBean
    private OrderRepository orderRepository;

    private static final LocalDateTime pointOfTime = LocalDateTime.of(2022, NOVEMBER, 21, 14, 3, 10);

    private static final Car car = Car.builder()
            .id(1)
            .mark("Audi")
            .model("A6")
            .rentPricePerHour(new BigDecimal("5.0"))
            .bookPricePerHour(new BigDecimal("2.0"))
            .imgLink("some-img-link")
            .status(FREE)
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

    private static final Order order1 = Order.builder()
            .car(car)
            .price(new BigDecimal("12"))
            .uuid(UUID.randomUUID().toString())
            .start(pointOfTime.minusMinutes(80))
            .ending(pointOfTime.minusMinutes(20))
            .status(OrderStatus.BOOKING)
            .build();

    private static final Order order2 = Order.builder()
            .car(car)
            .price(new BigDecimal("178"))
            .uuid(order1.getUuid())
            .start(pointOfTime.minusMinutes(20))
            .ending(pointOfTime.minusMinutes(1))
            .status(RENT)
            .build();

    private static final Order order3 = Order.builder()
            .car(car)
            .price(null)
            .uuid(order1.getUuid())
            .start(pointOfTime.minusMinutes(20))
            .status(RENT)
            .build();

    @Test
    void calculatePrice_Rent() {
        // given
        order1.setStatus(RENT);
        final BigDecimal expected = car.getRentPricePerHour().multiply(
                new BigDecimal(DateTimeUtil.retrieveDurationInMinutes(order1.getStart(), order1.getEnding()))
        );

        // verify
        assertThat(expected, Matchers.comparesEqualTo(priceService.calculatePrice(order1)));
    }

    @Test
    void calculatePrice_RentPaused() {
        // given
        order1.setStatus(OrderStatus.RENT_PAUSED);
        final BigDecimal expected = car.getRentPricePerHour().multiply(
                new BigDecimal(DateTimeUtil.retrieveDurationInMinutes(order1.getStart(), order1.getEnding()))
        );

        // verify
        assertThat(expected, Matchers.comparesEqualTo(priceService.calculatePrice(order1)));
    }

    @Test
    void calculatePrice_Booking() {
        // given
        order1.setStatus(OrderStatus.BOOKING);
        final BigDecimal expected = car.getBookPricePerHour().multiply(
                new BigDecimal(DateTimeUtil.retrieveDurationInMinutes(order1.getStart(), order1.getEnding()))
        );

        // verify
        assertThat(expected, Matchers.comparesEqualTo(priceService.calculatePrice(order1)));
    }

    @Test
    void calculateRidePrice_ShouldCalculate() {
        // given
        final BigDecimal expected = order1.getPrice().add(order2.getPrice());

        // when
        when(orderRepository.findAllByUuid(order1.getUuid())).thenReturn(List.of(order1, order2));

        // verify
        assertThat(expected, Matchers.comparesEqualTo(priceService.calculateRidePrice(order1.getUuid())));
    }

    @Test
    void calculateRidePrice_ShouldThrow() {
        // given
        final String orderUuid = order1.getUuid();

        // when
        when(orderRepository.findAllByUuid(order1.getUuid())).thenReturn(List.of(order1, order2, order3));

        // verify
        final OrderException thrown = assertThrows(
                OrderException.class,
                () -> priceService.calculateRidePrice(orderUuid)
        );
        assertEquals("Can not calculate price for not finished order", thrown.getMessage());
    }
}
