package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    public ResultDto create(final RegisterUserDto userDto) {
        if (userRepository.existsByEmail(userDto.email())) {
            throw new EntityNotFoundException("User already exists");
        }
        final User user = createUserFromDto(userDto);
        userRepository.save(user);
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

    private User createUserFromDto(final RegisterUserDto userDto) {
        return User.builder()
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

//    public ResultDto update(final UserDto userDto) {
//        final User foundUser = userRepository.findById(userDto.id())
//                .orElseThrow(() -> new EntityNotFoundException("Not found"));
//        foundUser.setFirstName(userDto.firstName());
//        foundUser.setLastName(userDto.lastName());
//        foundUser.setEmail(userDto.email());
//        foundUser.setPhone(userDto.phone());
//        foundUser.setRole(Role.valueOf(userDto.role()));
//        final User user = User.builder()
//                .id(userDto.id())
//                .firstName(userDto.firstName())
//                .lastName(userDto.lastName())
//                .email(userDto.email())
//                .phone(userDto.phone())
//                .role(Role.valueOf(userDto.role()))
//                .status(UserStatus.valueOf(userDto.status()))
//                .balance(userDto.balance())
//                .verified(userDto.verified())
//                .build();
//        if (userDto.id() != null) {
//
//            user.setDocument(foundUser.getDocument());
//            user.setPassword(foundUser.getPassword());
//        } else {
//            user.setPassword(passwordEncoder.encode(userDto.password()));
//        }
//        userRepository.save(user);
//        return new ResultDto("Successfully created your account, thank you!", true);
//    }

    private CompletableFuture<ResultDto> updateDocument(final MultipartFile file, final Consumer<String> updateImgUrl) {
        return fileService.uploadToS3(file).thenApply(url -> {
            url.ifPresent(updateImgUrl);
            return new ResultDto("Successfully uploaded the file", TRUE);
        });
    }
}
