package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.user.RolesDto;
import com.ostapchuk.car.rent.dto.user.StatusesDto;
import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.dto.user.UsersDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.BalanceException;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.ostapchuk.car.rent.util.Constant.ZERO_INT;
import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
public class UserReadService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findById(final Long id) {
        final User user = findVerifiedById(id);
        validateUser(user);
        return user;
    }

    public User findVerifiedById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with id: " + id));
    }

    public UsersDto findAll() {
        final List<UserDto> userDtos = userRepository.findAllByOrderById().stream()
                .map(userMapper::toDto)
                .toList();
        return new UsersDto(userDtos);
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public BigDecimal findBalanceById(final Long id) {
        return userRepository.findById(id).map(User::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public StatusesDto findAllStatuses() {
        return new StatusesDto(Set.of(UserStatus.values()));
    }

    public RolesDto findAllRoles() {
        return new RolesDto(Set.of(Role.values()));
    }

    public BigDecimal findDept(final Long userId) {
        final BigDecimal total = findById(userId).getBalance();
        if (total.compareTo(ZERO) >= ZERO_INT) {
            throw new BalanceException("The balance is positive. Nothing to pay");
        }
        return total.negate();
    }

    public void validateUser(final User user) {
        if (!user.isVerified()) {
            throw new UserUnverifiedException("You are not verified. Please, wait for the verification");
        }
        if (user.getBalance().compareTo(ZERO) < ZERO_INT) {
            throw new BalanceException("The balance is negative, please, pay the debt");
        }
    }
}
