package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.auth.AuthenticationRequestDto;
import com.ostapchuk.car.rent.dto.auth.AuthenticationResponseDto;
import com.ostapchuk.car.rent.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public record AuthenticationController(AuthenticationService authenticationService) {

    @PostMapping("/login")
    public AuthenticationResponseDto authenticate(@RequestBody final AuthenticationRequestDto request) {
        return authenticationService.login(request);
    }
}
