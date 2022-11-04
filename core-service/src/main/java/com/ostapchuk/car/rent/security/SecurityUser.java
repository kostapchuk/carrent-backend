package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.entity.Person;
import com.ostapchuk.car.rent.entity.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public static UserDetails fromUser(final Person person) {
        return new org.springframework.security.core.userdetails.User(
                person.getEmail(), person.getPassword(),
                person.getStatus().equals(UserStatus.ACTIVE),
                person.getStatus().equals(UserStatus.ACTIVE),
                person.getStatus().equals(UserStatus.ACTIVE),
                person.getStatus().equals(UserStatus.ACTIVE),
                person.getRole().getAuthorities()
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
