package com.ostapchuk.car.rent.exception;

public class OrderException extends RuntimeException {

    public OrderException(final String message) {
        super(message);
    }
}
