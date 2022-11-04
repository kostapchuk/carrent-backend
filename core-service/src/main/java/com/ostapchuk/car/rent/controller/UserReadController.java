package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.ride.RideDto;
import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.service.OrderReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserReadController {

    private final UserReadService userReadService;
    private final OrderReadService orderReadService;

    // TODO: 3/18/2022 check the same user
    @GetMapping("/api/v1/users/{id}/rides")
    @PreAuthorize("hasAuthority('users:read')")
    public List<RideDto> findAllRidesById(@PathVariable final Long id) {
        return orderReadService.findAllRidesByUserId(id);
    }

    @GetMapping("/api/v1/users/{id}/balance")
    @PreAuthorize("hasAuthority('users:read')")
    public BigDecimal findBalanceById(@PathVariable final Long id) {
        return userReadService.findBalanceById(id);
    }

    @GetMapping("/api/v1/users")
//    @PreAuthorize("hasAuthority('users:write')")
    public List<UserDto> findAll() {
        return userReadService.findAll();
    }

    @GetMapping("/api/v1/users/roles")
    @PreAuthorize("hasAuthority('users:write')")
    public Set<Role> findRoles() {
        return userReadService.findAllRoles();
    }

    @GetMapping("/api/v1/users/statuses")
    @PreAuthorize("hasAuthority('users:write')")
    public Set<UserStatus> findStatuses() {
        return userReadService.findAllStatuses();
    }

    @GetMapping("/api/v1/users/{id}/debt")
    @PreAuthorize("hasAuthority('users:read')")
    public BigDecimal findDebt(@PathVariable final Long id) {
        return userReadService.findDept(id);
    }
}
