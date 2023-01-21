package com.ostapchuk.car.rent.exception;

import com.ostapchuk.car.rent.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public record ExceptionAdviser() {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class, OrderException.class, BalanceException.class,
            UserUnverifiedException.class, CarUnavailableException.class})
    public ErrorResponse entityNotFoundException(final RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), BAD_REQUEST.value());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse accessDeniedException(final AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), FORBIDDEN.value());
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ErrorResponse jwtAuthenticationException(final JwtAuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), UNAUTHORIZED.value());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResponse generalException(final Throwable t) {
        log.error(t.getMessage(), t);
        return new ErrorResponse("Some error occurred: " + t, INTERNAL_SERVER_ERROR.value());
    }
}
