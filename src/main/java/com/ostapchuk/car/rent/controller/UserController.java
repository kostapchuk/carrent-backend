package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.RidesDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.dto.UsersDto;
import com.ostapchuk.car.rent.service.OrderService;
import com.ostapchuk.car.rent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    // TODO: 3/18/2022 check the same user
    @GetMapping("/{id}/rides")
    @PreAuthorize("hasAuthority('users:read')")
    public RidesDto findAllRidesById(@PathVariable final Long id) {
        return orderService.findAllRidesByUserId(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:write')")
    public UsersDto findAll() {
        return userService.findAll();
    }

    @PostMapping
    public ResultDto register(@RequestBody final UserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('users:write')")
    public ResultDto update(@RequestBody final UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public void update(@PathVariable final Long id) {
        userService.deleteById(id);
    }
}
