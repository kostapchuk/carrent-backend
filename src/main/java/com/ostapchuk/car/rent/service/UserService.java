package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.dto.UsersDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.NegativeBalanceException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.ostapchuk.car.rent.entity.Role.USER;
import static com.ostapchuk.car.rent.entity.UserStatus.ACTIVE;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id));
    }

    public void verifyUser(final User user) {
        if (!user.isVerified()) {
            throw new UserUnverifiedException("You are not verified. Please, wait for the verification");
        }
        if (user.getBalance().compareTo(ZERO) < 0) {
            throw new NegativeBalanceException("The balance is negative, please, pay the debt");
        }
    }

    public void deleteById(final Long id) {
        final User user = findById(id);
        userRepository.delete(user);
    }

    public UsersDto findAll() {
        final List<UserDto> userDtos = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        return new UsersDto(userDtos);
    }

    public ResultDto create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            return new ResultDto("Please, provide another email", false);
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
                .verified(false)
                .build();
        userRepository.save(user);
        return new ResultDto("Successfully created your account, thank you!", true);
    }

    public ResultDto update(final UserDto userDto) {
        final User user = User.builder()
                .id(userDto.id())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .phone(userDto.phone())
                .password(passwordEncoder.encode(userDto.password()))
                .role(Role.valueOf(userDto.role()))
                .status(UserStatus.valueOf(userDto.status()))
                .verified(userDto.verified())
                .build();
        userRepository.save(user);
        return new ResultDto("Successfully created your account, thank you!", true);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public BigDecimal findBalanceById(final Long id) {
        return userRepository.findById(id).map(User::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }
}
