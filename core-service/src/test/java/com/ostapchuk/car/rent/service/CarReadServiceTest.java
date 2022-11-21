package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.car.CarResponse;
import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.Order;
import com.ostapchuk.car.rent.entity.OrderStatus;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.CarMapper;
import com.ostapchuk.car.rent.repository.CarRepository;
import com.ostapchuk.car.rent.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.ostapchuk.car.rent.entity.CarStatus.FREE;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_BOOKING;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT;
import static com.ostapchuk.car.rent.entity.CarStatus.IN_RENT_PAUSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {CarReadService.class, CarMapper.class})
class CarReadServiceTest {

    @Autowired
    private CarReadService carReadService;
    @Autowired
    private CarMapper carMapper;
    @MockBean
    private CarRepository carRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserReadService userReadService;

    Car car = Car.builder()
            .id(1)
            .mark("Audi")
            .model("A6")
            .rentPricePerHour(new BigDecimal("5.0"))
            .bookPricePerHour(new BigDecimal("2.0"))
            .imgLink("some-img-link")
            .status(FREE)
            .build();

    Car car2 = Car.builder()
            .id(2)
            .mark("BMW")
            .model("M5")
            .rentPricePerHour(new BigDecimal("55.0"))
            .bookPricePerHour(new BigDecimal("25.0"))
            .imgLink("some-img-link")
            .status(FREE)
            .build();

    Car car3 = Car.builder()
            .id(3)
            .mark("Audi")
            .model("A8")
            .rentPricePerHour(new BigDecimal("515.0"))
            .bookPricePerHour(new BigDecimal("225.0"))
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

    @Test
    void findById_ShouldFind() {
        // when
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        // verify
        assertEquals(carMapper.toDto(car), carReadService.findById(car.getId()));
    }

    @Test
    void findById_ShouldThrow() {
        // given
        final Integer carId = car.getId() + 100;

        // when
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // verify
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> carReadService.findById(carId)
        );
        assertEquals("Could not find car with id: " + carId, thrown.getMessage());
    }

    @Test
    void findAllFree_ShouldFindThreeCars() {
        // given
        final List<Car> cars = List.of(car, car2, car3);

        // when
        when(carRepository.findAllByStatusOrderById(FREE)).thenReturn(cars);

        // verify
        final List<CarResponse> actual = carReadService.findAllFree();
        assertIterableEquals(cars.stream().map(carMapper::toDto).toList(), actual);
        // TODO: 21.11.2022 verify order
    }

    @Test
    void findAllFreeForUser_NoActiveOrder() {
        // given
        final List<Car> cars = List.of(car2, car3);

        // when
        when(orderRepository.findFirstByUserAndEndingIsNull(defaultUser)).thenReturn(Optional.empty());
        when(userReadService.findById(defaultUser.getId())).thenReturn(defaultUser);
        when(carRepository.findAllByStatusOrderById(FREE)).thenReturn(cars);

        // verify
        final List<CarResponse> actual = carReadService.findAllFreeForUser(defaultUser.getId());
        assertIterableEquals(cars.stream().map(carMapper::toDto).toList(), actual);
    }

    @Test
    void findAllFreeForUser_WithActiveOrder() {
        // given
        car.setStatus(IN_RENT);
        final List<Car> cars = List.of(car2, car3);
        final List<Car> expectedCars = List.of(car, car2, car3);
        final Order order = Order.builder()
                .ending(null)
                .start(LocalDateTime.now().minusMinutes(10))
                .status(OrderStatus.RENT)
                .uuid(UUID.randomUUID().toString())
                .car(car)
                .price(null)
                .build();

        // when
        when(orderRepository.findFirstByUserAndEndingIsNull(defaultUser)).thenReturn(Optional.of(order));
        when(userReadService.findById(defaultUser.getId())).thenReturn(defaultUser);
        when(carRepository.findAllByStatusOrderById(FREE)).thenReturn(cars);

        // verify
        final List<CarResponse> actual = carReadService.findAllFreeForUser(defaultUser.getId());
        assertIterableEquals(expectedCars.stream().map(carMapper::toDto).toList(), actual);
    }

    @Test
    void findStartableInBooking() {
        // when
        when(carRepository.findByIdAndStatus(car.getId(), FREE)).thenReturn(Optional.of(car));

        // verify
        final Optional<Car> actualBooking = carReadService.findStartable(car.getId(), IN_BOOKING);
        assertEquals(Optional.of(car), actualBooking);
    }

    @Test
    void findStartableInRent() {
        // when
        when(carRepository.findByIdAndStatus(car.getId(), FREE)).thenReturn(Optional.of(car));

        // verify
        final Optional<Car> actualRent = carReadService.findStartable(car.getId(), IN_RENT);
        assertEquals(Optional.of(car), actualRent);
    }

    @Test
    void findStartableRentPaused() {
        // verify
        final Optional<Car> actualRent = carReadService.findStartable(car.getId(), IN_RENT_PAUSED);
        verify(carRepository, times(0)).findByIdAndStatus(car.getId(), FREE);
        assertEquals(Optional.empty(), actualRent);
    }

    @Test
    void findStartableNotFree() {
        // given
        car.setStatus(IN_BOOKING);

        // when
        when(carRepository.findByIdAndStatus(car.getId(), FREE)).thenReturn(Optional.empty());

        // verify
        final Optional<Car> actualRent = carReadService.findStartable(car.getId(), IN_BOOKING);
        assertEquals(Optional.empty(), actualRent);
    }

    @Test
    void findUpdatable_FromBookingToRent() {
        // given
        car.setStatus(IN_BOOKING);

        // when
        when(carRepository.findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED))).thenReturn(
                Optional.of(car));

        // verify
        assertEquals(Optional.of(car), carReadService.findUpdatable(car.getId(), IN_RENT));
        verify(carRepository, times(0)).findByIdAndStatus(car.getId(), IN_RENT);
    }

    @Test
    void findUpdatable_FromRentToRentPaused() {
        // given
        car.setStatus(IN_RENT);

        // when
        when(carRepository.findByIdAndStatus(car.getId(), IN_RENT)).thenReturn(Optional.of(car));

        // verify
        assertEquals(Optional.of(car), carReadService.findUpdatable(car.getId(), IN_RENT_PAUSED));
        verify(carRepository, times(0)).findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED));
    }

    @Test
    void findUpdatable_FromRentPausedToRent() {
        // given
        car.setStatus(FREE);

        // verify
        assertEquals(Optional.empty(), carReadService.findUpdatable(car.getId(), FREE));
        verify(carRepository, times(0)).findByIdAndStatus(car.getId(), IN_RENT);
        verify(carRepository, times(0)).findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED));
    }

    @Test
    void findFinishable_FromBookingToFree() {
        // given
        car.setStatus(IN_BOOKING);

        // when
        when(carRepository.findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED, IN_RENT)))
                .thenReturn(Optional.of(car));

        // verify
        assertEquals(Optional.of(car), carReadService.findFinishable(car.getId(), FREE));
    }

    @Test
    void findFinishable_FromRentToFree() {
        // given
        car.setStatus(IN_RENT);

        // when
        when(carRepository.findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED, IN_RENT)))
                .thenReturn(Optional.of(car));

        // verify
        assertEquals(Optional.of(car), carReadService.findFinishable(car.getId(), FREE));
    }

    @Test
    void findFinishable_FromRentPausedToFree() {
        // given
        car.setStatus(IN_RENT_PAUSED);

        // when
        when(carRepository.findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED, IN_RENT)))
                .thenReturn(Optional.of(car));

        // verify
        assertEquals(Optional.of(car), carReadService.findFinishable(car.getId(), FREE));
    }

    @Test
    void findFinishable_NotFound() {
        // when
        when(carRepository.findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED, IN_RENT)))
                .thenReturn(Optional.empty());

        // verify
        assertEquals(Optional.empty(), carReadService.findFinishable(car.getId(), IN_RENT));
        verify(carRepository, times(0))
                .findByIdAndStatusIn(car.getId(), Set.of(IN_BOOKING, IN_RENT_PAUSED, IN_RENT));
    }
}
