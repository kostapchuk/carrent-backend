package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.user.UserDto;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserReadService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public User findById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The user with id " + id + " is not found"));
    }

    public UserDto findDtoById(final Long id) {
        return userMapper.toDto(findById(id));
    }

    public List<UserDto> findAll() {
        return userRepository.findAllByOrderById()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("The user with email " + email + " is not found"));
    }

    public UserDto findDtoByEmail(final String email) {
        return userMapper.toDto(findByEmail(email));
    }

    /**
     * The user is allowed to start if he is verified and balance not negative
     *
     * @param userId
     * @return
     */
    public Optional<User> findAllowedToStartRideById(final Long userId) {
        return Optional.of(findById(userId)).filter(user -> user.getBalance().compareTo(BigDecimal.ZERO) >= 0);
    }
}
