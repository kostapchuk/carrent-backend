package com.ostapchuk.car.rent.exception;

import com.ostapchuk.car.rent.dto.ErrorResponseDto;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public record ExceptionAdviser() {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class, OrderCreationException.class, NegativeBalanceException.class,
            UserUnverifiedException.class})
    public ErrorResponseDto entityNotFoundException(final RuntimeException ex) {
        return new ErrorResponseDto(ex.getMessage(), BAD_REQUEST.value());
    }
}
