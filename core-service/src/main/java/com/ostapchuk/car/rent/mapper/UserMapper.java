package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.User;
import org.springframework.stereotype.Component;

@Component
public record UserMapper() {

    // TODO: 10.11.2022 separate dtos to request/response classes
    public UserDto toDto(final User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail(),
                null,
                user.getStatus().toString(),
                user.isVerified(),
                user.getRole().toString(),
                user.getBalance(),
                user.getPassportImgUrl(),
                user.getDrivingLicenseImgUrl()
        );
    }
}