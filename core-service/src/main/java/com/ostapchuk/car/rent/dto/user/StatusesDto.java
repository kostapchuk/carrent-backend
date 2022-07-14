package com.ostapchuk.car.rent.dto.user;

import com.ostapchuk.car.rent.entity.UserStatus;

import java.util.Set;

public record StatusesDto(Set<UserStatus> statuses) {
}
