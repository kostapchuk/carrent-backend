package com.ostapchuk.car.rent.exception;

public class NegativeBalanceException extends RuntimeException {

    public NegativeBalanceException(final String message) {
        super(message);
    }
}
