package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.BalanceException;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import com.ostapchuk.car.rent.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.ZERO;

@Service
public record UserReadService(
        UserRepository userRepository,
        UserMapper userMapper
) {

    public User findById(final Long id) {
        final User user = findVerifiedById(id);
        validateUser(user);
        return user;
    }

    public User findVerifiedById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with id: " + id));
    }

    public List<UserDto> findAll() {
        return userRepository.findAllByOrderById()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public BigDecimal findBalanceById(final Long id) {
        return userRepository.findById(id)
                .map(User::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public Set<UserStatus> findAllStatuses() {
        return Set.of(UserStatus.values());
    }

    public Set<Role> findAllRoles() {
        return Set.of(Role.values());
    }

    public BigDecimal findDept(final Long userId) {
        final BigDecimal total = findById(userId).getBalance();
        if (total.compareTo(ZERO) >= Constant.ZERO_INT) {
            throw new BalanceException("The balance is positive. Nothing to pay");
        }
        return total.negate();
    }

    public void validateUser(final User user) {
        if (!user.isVerified()) {
            throw new UserUnverifiedException("You are not verified. Please, wait for the verification");
        }
        if (user.getBalance()
                .compareTo(ZERO) < Constant.ZERO_INT) {
            throw new BalanceException("The balance is negative, please, pay the debt");
        }
    }
}
