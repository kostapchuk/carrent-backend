package com.ostapchuk.car.rent.dto;

public record AuthenticationResponseDto(
        String email,
        String token
) {
}
