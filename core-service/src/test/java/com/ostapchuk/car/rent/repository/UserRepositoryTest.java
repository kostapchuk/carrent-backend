package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.properties.CloudinaryProperties;
import com.ostapchuk.car.rent.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test-jpa")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    @MockBean
    private CloudinaryProperties cloudinaryProperties;
    @MockBean
    private CloudinaryProperties.CloudinaryApiProperties cloudinaryApiProperties;
    @MockBean
    private JwtProperties jwtProperties;

    @BeforeEach
    protected void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
//    @Sql(scripts = {"/insert-user.sql"})
    void resetBalance_ShouldReset() {
        // given
        defaultUser.setBalance(new BigDecimal("10"));
        final Long userId = entityManager.persistAndFlush(defaultUser).getId();
        entityManager.clear();

        // verify
        assertThat(userRepository.findByEmail(defaultUser.getEmail()).map(User::getBalance)
                .filter(b -> b.compareTo(BigDecimal.ZERO) > 0)).isPresent();
        userRepository.resetBalance(userId);
        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findByEmail(defaultUser.getEmail()).map(User::getBalance)
                .filter(b -> b.compareTo(BigDecimal.ZERO) == 0)).isPresent();
    }

    // TODO: 10.11.2022 fix test, add sql annotation with the script from resources
    @Test
    @Disabled("Two tests are not working under a single run because User gets detached")
    void resetBalance_ShouldNotReset() {
        // given
        defaultUser.setBalance(new BigDecimal("10"));
        final Long userId = entityManager.persistAndFlush(defaultUser).getId();
        entityManager.clear();

        // verify
        assertThat(userRepository.findByEmail(defaultUser.getEmail()).map(User::getBalance)
                .filter(b -> b.compareTo(BigDecimal.ZERO) > 0)).isPresent();
        userRepository.resetBalance(userId + 1);
        entityManager.flush();
        entityManager.clear();
        assertThat(userRepository.findByEmail(defaultUser.getEmail()).map(User::getBalance)
                .filter(b -> b.compareTo(BigDecimal.ZERO) > 0)).isPresent();
    }

    private static final User defaultUser = User.builder()
            .firstName("FirstName")
            .lastName("LastName")
            .phone("+375332225544")
            .email("user@mailer.com")
            .password("passwordHash1231234")
            .verified(true)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();
}
