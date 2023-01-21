package com.ostapchuk.car.rent.converter;

import com.ostapchuk.car.rent.entity.CarStatus;
import com.ostapchuk.car.rent.exception.BadStatusException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link StatusConverter}
 */
@SpringJUnitConfig(classes = {StatusConverter.class})
class StatusConverterTest {

    @Autowired
    private StatusConverter statusConverter;

    /**
     * {@link StatusConverter#toOrderStatus(CarStatus)}
     */
    @Test
    void toOrderStatus_ShouldConvert() {
        assertThat(statusConverter.toOrderStatus(CarStatus.IN_BOOKING).name()).isNotBlank();
        assertThat(statusConverter.toOrderStatus(CarStatus.IN_RENT).name()).isNotBlank();
        assertThat(statusConverter.toOrderStatus(CarStatus.IN_RENT_PAUSED).name()).isNotBlank();
    }

    /**
     * {@link StatusConverter#toOrderStatus(CarStatus)}
     */
    @Test
    void toOrderStatus_ShouldThrowException() {
        final BadStatusException thrownFree = assertThrows(
                BadStatusException.class,
                () -> statusConverter.toOrderStatus(CarStatus.FREE)
        );
        final BadStatusException thrownUnavailable = assertThrows(
                BadStatusException.class,
                () -> statusConverter.toOrderStatus(CarStatus.UNAVAILABLE)
        );
        assertEquals(thrownFree.getMessage(), "Could not convert car status " + CarStatus.FREE);
        assertEquals(thrownUnavailable.getMessage(), "Could not convert car status " + CarStatus.UNAVAILABLE);
    }
}
