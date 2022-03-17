package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.exception.NegativeBalanceException;
import com.ostapchuk.car.rent.exception.UserUnverifiedException;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find car with id: " + id));
    }

    public void verifyUser(final User user) {
        if (!user.isVerified()) {
            throw new UserUnverifiedException("You are not verified. Please, wait for the verification");
        }
        if (user.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException("The balance is negative, please, pay the debt");
        }
    }
}
