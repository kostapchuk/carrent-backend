package com.ostapchuk.car.rent.exception;

public class CarUnavailableException extends RuntimeException {
    public CarUnavailableException(final String message) {
        super(message);
    }
}
