package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.RidesDto;
import com.ostapchuk.car.rent.service.OrderService;
import com.ostapchuk.car.rent.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public record UserController(UserService userService, OrderService orderService) {

    @GetMapping("/{id}/rides")
    public RidesDto findAllRidesById(@PathVariable final Long id) {
        return orderService.findAllRidesByUserId(id);
    }
}
