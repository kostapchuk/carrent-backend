package com.ostapchuk.car.rent.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ostapchuk.car.rent.entity.Permission.USERS_DELETE;
import static com.ostapchuk.car.rent.entity.Permission.USERS_READ;
import static com.ostapchuk.car.rent.entity.Permission.USERS_WRITE;

public enum Role {

    USER(Collections.singleton(USERS_READ)),
    ADMIN(Set.of(USERS_READ, USERS_WRITE, USERS_DELETE));

    private final Set<Permission> permissions;

    Role(final Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return this.permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }
}
