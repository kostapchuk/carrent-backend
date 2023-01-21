package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserReadService userReadService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return SecurityUser.fromUser(userReadService.findByEmail(email));
    }
}
