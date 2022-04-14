package com.ostapchuk.car.rent.exception;

public class PayPalException extends RuntimeException {

    public PayPalException(final String message) {
        super(message);
    }
}
