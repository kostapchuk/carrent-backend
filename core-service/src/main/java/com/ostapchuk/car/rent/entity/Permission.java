package com.ostapchuk.car.rent.entity;

// TODO: 20.11.2022 move from permissions to role only 
public enum Permission {

    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    USERS_DELETE("users:delete");

    private final String name;

    Permission(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
