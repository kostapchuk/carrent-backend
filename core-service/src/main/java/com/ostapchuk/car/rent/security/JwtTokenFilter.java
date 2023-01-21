package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<HttpMethod, String[]> publicEndpoints;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return publicEndpoints.entrySet().stream().flatMap(entry -> Arrays.stream(entry.getValue())
                        .map(value -> new AntPathRequestMatcher(value, entry.getKey().name())))
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
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
