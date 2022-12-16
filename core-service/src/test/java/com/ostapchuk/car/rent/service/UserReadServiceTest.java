package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link UserReadService}
 */
@SpringJUnitConfig(classes = {UserReadService.class, UserMapper.class})
class UserReadServiceTest {

    @Autowired
    private UserReadService userReadService;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserRepository userRepository;

    private static final User defaultUser = User.builder()
            .id(1L)
            .firstName("FirstName")
            .lastName("LastName")
            .phone("+375332225544")
            .email("user@mailer.com")
            .password("passwordHash1231234")
            .verified(true)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();

    @Test
    void findById_ShouldFind() {
        // when
        when(userRepository.findById(defaultUser.getId())).thenReturn(Optional.of(defaultUser));

        // verify
        assertEquals(defaultUser, userReadService.findById(defaultUser.getId()));
    }

    @Test
    void findById_ShouldNotFind() {
        // given
        final Long badUserId = defaultUser.getId() + 10L;

        // when
        when(userRepository.findById(badUserId)).thenReturn(Optional.empty());

        // verify
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userReadService.findById(badUserId)
        );
        assertEquals("The user with id " + badUserId + " is not found", thrown.getMessage());
    }

    @Test
    void findByEmail_ShouldFind() {
        // when
        when(userRepository.findByEmail(defaultUser.getEmail())).thenReturn(Optional.of(defaultUser));

        // verify
        assertEquals(defaultUser, userReadService.findByEmail(defaultUser.getEmail()));
    }

    @Test
    void findByEmail_ShouldThrow() {
        // given
        final String badUserEmail = defaultUser.getEmail() + "asdas";

        // when
        when(userRepository.findByEmail(badUserEmail)).thenReturn(Optional.empty());

        // verify
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userReadService.findByEmail(badUserEmail)
        );
        assertEquals("The user with email " + badUserEmail + " is not found", thrown.getMessage());
    }

    @Test
    void findDtoById() {
        // when
        when(userRepository.findById(defaultUser.getId())).thenReturn(Optional.of(defaultUser));

        // verify
        assertEquals(userMapper.toDto(defaultUser), userReadService.findDtoById(defaultUser.getId()));

    }

    @Test
    void findDtoByEmail() {
        // when
        when(userRepository.findByEmail(defaultUser.getEmail())).thenReturn(Optional.of(defaultUser));

        // verify
        assertEquals(userMapper.toDto(defaultUser), userReadService.findDtoByEmail(defaultUser.getEmail()));
    }


    /**
     * {@link UserReadService#findAllowedToStartRideById(Long)}
     */
    @Test
    void findAllowedToStartRideById_WhenBalancePositive() {
        // given
        final User expected = defaultUser;
        expected.setBalance(new BigDecimal("5.00"));

        // when
        when(userRepository.findById(defaultUser.getId())).thenReturn(Optional.of(expected));

        // verify
        final Optional<User> actual = userReadService.findAllowedToStartRideById(defaultUser.getId());
        assertTrue(actual.isPresent());
        assertTrue(actual.get().getBalance().compareTo(ZERO) >= 0);
    }

    /**
     * {@link UserReadService#findAllowedToStartRideById(Long)}
     */
    @Test
    void findAllowedToStartRideById_WhenBalanceNegative_ShouldNotFind() {
        // given
        final User expected = defaultUser;
        expected.setBalance(new BigDecimal("-5.00"));

        // when
        when(userRepository.findById(defaultUser.getId())).thenReturn(Optional.of(expected));

        // verify
        final Optional<User> actual = userReadService.findAllowedToStartRideById(defaultUser.getId());
        assertEquals(Optional.empty(), actual);
    }
}
