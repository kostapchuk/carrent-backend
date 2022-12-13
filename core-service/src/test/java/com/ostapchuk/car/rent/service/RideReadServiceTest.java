package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.ride.RideDetailsResponse;
import com.ostapchuk.car.rent.dto.ride.RideResponse;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.mapper.OrderMapper;
import com.ostapchuk.car.rent.repository.OrderRepository;
import com.ostapchuk.car.rent.util.DateTimeUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {RideReadService.class, OrderMapper.class, PriceService.class})
class RideReadServiceTest {

    @Autowired
    private RideReadService rideReadService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PriceService priceService;
    @MockBean
    private UserReadService userReadService;
    @MockBean
    private OrderRepository orderRepository;

    private static final LocalDateTime pointOfTime = LocalDateTime.now();

    private static final Car car = Car.builder()
            .id(1)
            .mark("Audi")
            .model("A6")
            .rentPricePerHour(new BigDecimal("5.0"))
            .bookPricePerHour(new BigDecimal("2.0"))
            .imgLink("some-img-link")
            .status(FREE)
            .build();

    private static final Car car2 = Car.builder()
            .id(2)
            .mark("BMW")
            .model("M5")
            .rentPricePerHour(new BigDecimal("55.0"))
            .bookPricePerHour(new BigDecimal("25.0"))
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
            .status(OrderStatus.RENT)
            .build();

    private static final Order order3 = Order.builder()
            .car(car2)
            .price(new BigDecimal("312"))
            .uuid(UUID.randomUUID().toString())
            .start(pointOfTime.minusMinutes(280))
            .ending(pointOfTime.minusMinutes(260))
            .status(OrderStatus.BOOKING)
            .build();

    private static final Order order4 = Order.builder()
            .car(car2)
            .price(new BigDecimal("256"))
            .uuid(order3.getUuid())
            .start(pointOfTime.minusMinutes(260))
            .ending(pointOfTime.minusMinutes(200))
            .status(OrderStatus.RENT)
            .build();

    @Test
    void findAllRidesByUserId() {
        // given
        final List<Order> orders = List.of(order1, order2, order3, order4);

        // when
        when(userReadService.findById(defaultUser.getId())).thenReturn(defaultUser);
        when(orderRepository.findAllByUserAndEndingIsNotNullOrderByStartAsc(defaultUser))
                .thenReturn(orders);

        // verify
//        final var actual = rideReadService.findAllRidesByUserId(defaultUser.getId());
        new RideResponse(
                LocalDate.now(),
                car2.getMark(),
                car2.getModel(),
                order3.getPrice().add(order4.getPrice()),
                DateTimeUtil.retrieveDurationInMinutes(order3.getStart(), order3.getEnding()) +
                        DateTimeUtil.retrieveDurationInMinutes(order4.getStart(), order4.getEnding()),
                List.of(new RideDetailsResponse())
        );
//        assertIterableEquals(, actual);
    }

    @Disabled
    @Test
    void findAllRidesByUserId_ThrowsException() {

        // when
        when(userReadService.findById(defaultUser.getId())).thenReturn(defaultUser);
        when(orderRepository.findAllByUserAndEndingIsNotNullOrderByStartAsc(defaultUser))
                .thenReturn(List.of(order1, order2, order3, order4));

        // verify
//        List<RideResponse> rideResponses = List.of(new RideResponse(, car.getMark(), car.getModel(), new BigDecimal(
//                "190"), 4740, ));
        var actual = rideReadService.findAllRidesByUserId(defaultUser.getId());
        System.out.println(actual);
    }

//    @Test
//    void testFindAllRidesByUserId() {
//    }
}
