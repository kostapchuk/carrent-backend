package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.user.RegisterUserDto;
import com.ostapchuk.car.rent.entity.Person;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.repository.UserRepository;
import com.ostapchuk.car.rent.service.file.FileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.ostapchuk.car.rent.entity.Role.USER;
import static com.ostapchuk.car.rent.entity.UserStatus.ACTIVE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;

@Service
public record UserWriteService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        FileService fileService
) {

    public ResultDto create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new EntityNotFoundException("User already exists");
        }
        final Person person = createUserFromDto(userDto);
        userRepository.save(person);
        return new ResultDto("Successfully created your account, thank you!", TRUE);
    }

    public void payDebt(final Long userId) {
        userRepository.resetBalance(userId);
    }

    public void deleteById(final Long id) {
        userRepository.deleteById(id);
    }

    public CompletableFuture<ResultDto> updatePassportDocument(final MultipartFile file, final Long userId) {
        return updateDocument(file, url -> userRepository.updatePassportUrl(userId, url));
    }

    public CompletableFuture<ResultDto> updateDrivingLicenseDocument(final MultipartFile file, final Long userId) {
        return updateDocument(file, url -> userRepository.updateDrivingLicenseUrl(userId, url));
    }

    private Person createUserFromDto(final RegisterUserDto userDto) {
        return Person.builder()
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
    }

    private CompletableFuture<ResultDto> updateDocument(final MultipartFile file, final Consumer<String> updateImgUrl) {
        return fileService.upload(file)
                .thenApply(url -> {
                    url.ifPresent(updateImgUrl);
                    return new ResultDto("Successfully uploaded the file", TRUE);
                });
    }
}
