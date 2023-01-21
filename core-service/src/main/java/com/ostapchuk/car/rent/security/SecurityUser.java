package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static com.ostapchuk.car.rent.entity.UserStatus.ACTIVE;

@Getter
@RequiredArgsConstructor
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public static UserDetails fromUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                ACTIVE.equals(user.getStatus()),
                ACTIVE.equals(user.getStatus()),
                ACTIVE.equals(user.getStatus()),
                ACTIVE.equals(user.getStatus()),
                user.getRole().getAuthorities()
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
