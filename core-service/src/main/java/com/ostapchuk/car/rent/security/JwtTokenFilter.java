package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final List<String> skipFilterUrls =
            Arrays.asList("/api/v1/cars", "/api/v1/cars/free", "/api/v1/cars/*", "/api/v1/auth/**",
                    "/api/v1/cars/available/*", "/api/v1/users", "/api/v1/payments/**");
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return skipFilterUrls.stream().map(AntPathRequestMatcher::new).anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    @SneakyThrows
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) {
        try {
            final String token = jwtTokenProvider.resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
            }
            filterChain.doFilter(request, response);
        } catch (final JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }
}
