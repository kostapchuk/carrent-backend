package com.ostapchuk.car.rent.mapper;

import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.Person;
import org.springframework.stereotype.Component;

@Component
public record UserMapper() {

    public UserDto toDto(final Person person) {
        return new UserDto(person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getPhone(),
                person.getEmail(),
                null,
                person.getStatus().toString(),
                person.isVerified(),
                person.getRole().toString(),
                person.getBalance(),
                person.getPassportImgUrl(),
                person.getDrivingLicenseImgUrl()
        );
    }
}
