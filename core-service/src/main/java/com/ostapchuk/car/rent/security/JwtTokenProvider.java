package com.ostapchuk.car.rent.security;

import com.ostapchuk.car.rent.exception.JwtAuthenticationException;
import com.ostapchuk.car.rent.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private String secretKey;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.secret().getBytes());
    }

    public String createToken(final String userName, final String role) {
        final Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role", role);
        final Date now = new Date();
        final Date validity = retrieveValidityDate();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(validity)
                .signWith(HS256, secretKey)
                .compact();
    }

    boolean validateToken(final String token) {
        try {
            final Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return !claimsJws.getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (final JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }

    Authentication getAuthentication(final String token) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(retrieveUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, EMPTY, userDetails.getAuthorities());
    }

    String resolveToken(final HttpServletRequest request) {
        return request.getHeader(jwtProperties.header());
    }

    private String retrieveUserName(final String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Date retrieveValidityDate() {
        return Date.from(
                now().plusSeconds(jwtProperties.accessTokenExpiration().toSeconds()).atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
