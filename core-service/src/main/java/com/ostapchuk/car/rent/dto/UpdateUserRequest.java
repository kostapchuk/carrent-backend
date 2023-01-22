package com.ostapchuk.car.rent.dto;

import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.UserStatus;

public record UpdateUserRequest(UserStatus status, Role role, boolean verified) {
}
