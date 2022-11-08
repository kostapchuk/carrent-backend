package com.ostapchuk.car.rent.entity;

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