package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.auth.LoginRequest;
import com.ostapchuk.car.rent.dto.auth.LoginResponse;
import com.ostapchuk.car.rent.entity.User;
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
        final User user = userReadService.findByEmail(request.email());
        final String token = jwtTokenProvider.createToken(request.email(), user.getRole().name());
        return new LoginResponse(user.getId(), token, user.getRole().name());
    }
}
