package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.ride.RidesDto;
import com.ostapchuk.car.rent.dto.user.RolesDto;
import com.ostapchuk.car.rent.dto.user.StatusesDto;
import com.ostapchuk.car.rent.dto.user.UsersDto;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class UserReadController {

    private final UserReadService userReadService;
    private final OrderReadService orderReadService;

    // TODO: 3/18/2022 check the same user
    @GetMapping("/api/v1/users/{id}/rides")
    @PreAuthorize("hasAuthority('users:read')")
    public RidesDto findAllRidesById(@PathVariable final Long id) {
        return orderReadService.findAllRidesByUserId(id);
    }

    @GetMapping("/api/v1/users/{id}/balance")
    @PreAuthorize("hasAuthority('users:read')")
    public BigDecimal findBalanceById(@PathVariable final Long id) {
        return userReadService.findBalanceById(id);
    }

    @GetMapping("/api/v1/users")
    public UsersDto findAll() {
        return userReadService.findAll();
    }

    @GetMapping("/api/v1/users/roles")
    @PreAuthorize("hasAuthority('users:write')")
    public RolesDto findRoles() {
        return userReadService.findAllRoles();
    }

    @GetMapping("/api/v1/users/statuses")
    @PreAuthorize("hasAuthority('users:write')")
    public StatusesDto findStatuses() {
        return userReadService.findAllStatuses();
    }

    @GetMapping("/api/v1/users/{id}/debt")
    @PreAuthorize("hasAuthority('users:read')")
    public BigDecimal findDebt(@PathVariable final Long id) {
        return userReadService.findDept(id);
    }
}
