package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.auth.LoginRequest;
import com.ostapchuk.car.rent.dto.auth.LoginResponse;
import com.ostapchuk.car.rent.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public record AuthenticationController(AuthenticationService authenticationService) {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody final LoginRequest request) {
        return authenticationService.login(request);
    }

    // TODO: 20.11.2022 refresh token
}
