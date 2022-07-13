package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ostapchuk.car.rent.entity.Role.USER;
import static com.ostapchuk.car.rent.entity.UserStatus.ACTIVE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@RequiredArgsConstructor
public class UserWriteService {

    @Value("${spring.kafka.template.email-topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final UserRepository userRepository;
    private final UserReadService userReadService;
    private final PasswordEncoder passwordEncoder;

    public void deleteById(final Long id) {
        final User user = userReadService.findById(id);
        userRepository.delete(user);
    }

    public ResultDto create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            return new ResultDto("Please, provide another email", FALSE);
        }
        final User user = User.builder()
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .balance(ZERO)
                .password(passwordEncoder.encode(userDto.password()))
                .role(USER)
                .status(ACTIVE)
                .verified(FALSE)
                .build();
        userRepository.save(user);
        kafkaTemplate.send(topic, EMPTY);
        return new ResultDto("Successfully created your account, thank you!", TRUE);
    }

    public ResultDto update(final UserDto userDto) {
        final User user = User.builder()
                .id(userDto.id())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .role(Role.valueOf(userDto.role()))
                .status(UserStatus.valueOf(userDto.status()))
                .balance(userDto.balance())
                .verified(userDto.verified())
                .build();
        if (userDto.id() != null) {
            final User foundUser = userRepository.findById(userDto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Not found"));
            user.setDocument(foundUser.getDocument());
            user.setPassword(foundUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        userRepository.save(user);
        return new ResultDto("Successfully created your account, thank you!", true);
    }

    public void payDebt(final Long userId) {
        final User user = userReadService.findById(userId);
        user.setBalance(ZERO);
        userRepository.save(user);
    }
}
