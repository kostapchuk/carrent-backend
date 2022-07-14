package com.ostapchuk.car.rent.dto.user;

public record RegisterUserDto(
        String firstName,
        String lastName,
        String phone,
        String email,
        String password
) {
}
