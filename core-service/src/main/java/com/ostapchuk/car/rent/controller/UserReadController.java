package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.ride.RideResponse;
import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.service.RideReadService;
import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserReadController {

    private final RideReadService rideReadService;
    private final UserReadService userReadService;

    @GetMapping("/api/v1/users/{id}/rides")
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_READ.getName())")
    public List<RideResponse> findAllRides(@PathVariable final Long id) {
        return rideReadService.findAllRidesByUserId(id);
    }

    @GetMapping("/api/v1/users/{id}/balance")
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_READ.getName())")
    public BigDecimal findBalance(@PathVariable final Long id) {
        return userReadService.findDtoById(id).balance();
    }

    @GetMapping("/api/v1/users")
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_WRITE.getName())")
    public List<UserDto> findAll() {
        return userReadService.findAll();
    }

    @GetMapping("/api/v1/users/roles")
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_WRITE.getName())")
    public Role[] findRoles() {
        return Role.values();
    }

    @GetMapping("/api/v1/users/statuses")
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_WRITE.getName())")
    public UserStatus[] findStatuses() {
        return UserStatus.values();
    }
}
