package com.ostapchuk.car.rent.dto.auth;

public record AuthenticationResponseDto(
        Long userId,
        String token,
        String role
) {
}
