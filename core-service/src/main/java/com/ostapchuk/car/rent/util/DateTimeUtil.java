package com.ostapchuk.car.rent.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class DateTimeUtil {

    public int retrieveDurationInHours(final LocalDateTime start, final LocalDateTime end) {
        final Duration duration = Duration.between(start, end);
        return (int) TimeUnit.SECONDS.toHours(duration.getSeconds());
    }
}
