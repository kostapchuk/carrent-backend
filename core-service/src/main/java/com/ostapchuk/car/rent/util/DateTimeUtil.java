package com.ostapchuk.car.rent.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public class DateTimeUtil {

    public int retrieveDurationInMinutes(final LocalDateTime start, final LocalDateTime end) {
        final Duration duration = Duration.between(start, end);
        return (int) duration.getSeconds() / 60;
    }
}
