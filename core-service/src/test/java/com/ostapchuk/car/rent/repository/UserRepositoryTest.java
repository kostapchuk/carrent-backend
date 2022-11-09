package com.ostapchuk.car.rent.repository;

import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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

    @BeforeEach
    protected void beforeEach() {
        userRepository.deleteAll();
    }

    private static final User defaultUser = User.builder()
            .firstName("FirstName")
            .lastName("LastName")
            .phone("+375332225544")
            .email("user@mailer.com")
            .password("passwordHash1231234")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .verified(true)
            .balance(BigDecimal.ZERO)
            .passportImgUrl("someurl")
            .drivingLicenseImgUrl("someurl")
            .build();

    @Test
    @Sql(value = "/insert-user.sql")
    void resetBalance() {
//        Long userId = entityManager.persistAndFlush(defaultUser).getId();
//        entityManager.clear();
//        final User user = userRepository.save(defaultUser);
//        assertThat(userId).isNotNull();
//        assertThat(userRepository.findByEmail("email@mail.com")).isPresent();
        assertThat(userRepository.findAll().iterator().hasNext()).isTrue();
    }

    @Test
    void saveUserTest() {
//        userRepository.deleteAll();
        final User user = userRepository.save(defaultUser);
        assertThat(user.getId()).isNotNull();
    }

//    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//        @Override
//        public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues
//                    .of("spring.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
//                            "spring.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
//                            "spring.datasource.password=" + POSTGRESQL_CONTAINER.getPassword())
//                    .applyTo(configurableApplicationContext.getEnvironment());
//        }
//    }
}
