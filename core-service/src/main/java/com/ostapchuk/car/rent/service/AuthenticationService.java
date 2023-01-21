package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.auth.LoginRequest;
import com.ostapchuk.car.rent.dto.auth.LoginResponse;
import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public record AuthenticationService(
        AuthenticationManager authenticationManager,
        UserReadService userReadService,
        JwtTokenProvider jwtTokenProvider
) {
    public LoginResponse login(final LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        final UserDto user = userReadService.findDtoByEmail(request.email());
        final String token = jwtTokenProvider.createToken(request.email(), user.role());
        return new LoginResponse(user.id(), token, user.role());
    }
}
