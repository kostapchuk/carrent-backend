package com.ostapchuk.car.rent.dto.auth;

public record LoginResponse(
        Long userId,
        String token,
        String role
) {
}
