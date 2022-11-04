package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.auth.AuthenticationRequestDto;
import com.ostapchuk.car.rent.dto.auth.AuthenticationResponseDto;
import com.ostapchuk.car.rent.entity.Person;
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
    public AuthenticationResponseDto login(final AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        final Person person = userReadService.findByEmail(request.email());
        final String token = jwtTokenProvider.createToken(request.email(), person.getRole().name());
        return new AuthenticationResponseDto(person.getId(), token, person.getRole().name());
    }
}
