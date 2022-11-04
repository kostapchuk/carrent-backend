package com.ostapchuk.car.rent.processor;

import com.ostapchuk.car.rent.entity.Car;
import com.ostapchuk.car.rent.entity.User;
import org.junit.jupiter.api.Test;

class StartingRideStatusProcessorTest {

//    private final OrderDto orderDto = new OrderDto();
    private final Car car = new Car();
    private final User user = new User();

    @Test
    void process_WhenCarIsFreeAndUserIsVerified_ShouldStart() {

    }

    @Test
    void process_WhenCarIsNotFreeAndUserIsVerified_ShouldNotStart() {

    }

    @Test
    void process_WhenCarIsFreeAndUserIsNotVerified_ShouldNotStart() {

    }
}
