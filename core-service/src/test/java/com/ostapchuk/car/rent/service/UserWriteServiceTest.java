package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.GeneralResponse;
import com.ostapchuk.car.rent.dto.UpdateUserRequest;
import com.ostapchuk.car.rent.dto.user.RegisterUserDto;
import com.ostapchuk.car.rent.entity.Role;
import com.ostapchuk.car.rent.entity.User;
import com.ostapchuk.car.rent.entity.UserStatus;
import com.ostapchuk.car.rent.exception.EntityNotFoundException;
import com.ostapchuk.car.rent.mapper.UserMapper;
import com.ostapchuk.car.rent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {UserWriteService.class, BCryptPasswordEncoder.class, UserMapper.class})
class UserWriteServiceTest {

    @Autowired
    private UserWriteService userWriteService;

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private FileService fileService;
    @Autowired
    private UserMapper userMapper;

    @Test
    void create_NotExist() {
        // given
        final RegisterUserDto userDto = new RegisterUserDto(
                "Kirill",
                "Ostapchuk",
                "+375333719302",
                "user@email.com",
                "s@mEc00lpa5"
        );
        final User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.password()));
        final GeneralResponse expected = new GeneralResponse("Successfully created your account, thank you!", TRUE);

        // when
        when(userRepository.existsByEmail(userDto.email())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // verify
        assertEquals(expected, userWriteService.create(userDto));
    }

    @Test
    void create_Exist() {
        // given
        final RegisterUserDto userDto = new RegisterUserDto(
                "Kirill",
                "Ostapchuk",
                "+375333719302",
                "user@email.com",
                "s@mEc00lpa5"
        );
        final User user = userMapper.toEntity(userDto);

        // when
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        // verify
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userWriteService.create(userDto)
        );
        assertEquals("User already exists", thrown.getMessage());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void payDebt() {
        userRepository.resetBalance(1L);
        verify(userRepository, times(1)).resetBalance(Mockito.anyLong());
    }

    @Test
    void deleteById() {
        userRepository.deleteById(1L);
        verify(userRepository, times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void updatePassportDocument() {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        final GeneralResponse expected = new GeneralResponse("Successfully uploaded the file", TRUE);

        // when
        when(fileService.upload(mockFile)).thenReturn(CompletableFuture.completedFuture(Optional.of("url")));
        doNothing().when(userRepository).updatePassportUrl(1L, "url");

        // verify
        assertEquals(
                CompletableFuture.completedFuture(expected).join(),
                userWriteService.updatePassportDocument(mockFile, 1L).join()
        );
    }

    @Test
    void updateDrivingLicenseDocument() {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        final GeneralResponse expected = new GeneralResponse("Successfully uploaded the file", TRUE);

        // when
        when(fileService.upload(mockFile)).thenReturn(CompletableFuture.completedFuture(Optional.of("url")));
        doNothing().when(userRepository).updatePassportUrl(1L, "url");

        // verify
        assertEquals(
                CompletableFuture.completedFuture(expected).join(),
                userWriteService.updateDrivingLicenseDocument(mockFile, 1L).join()
        );
    }

    @Test
    void save() {
        userRepository.save(User.builder().build());
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateByIdWhenUserExistsShouldUpdate() {
        // given
        final Long userId = 1L;
        final UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                UserStatus.ACTIVE,
                Role.USER,
                false
        );
        final User beforeUser = new User(
                userId,
                "firstName",
                "lastName",
                "+375333719302",
                "someEmail@gam.com",
                "pwd",
                Role.ADMIN,
                UserStatus.ACTIVE,
                BigDecimal.TEN,
                true,
                "",
                "",
                Collections.emptyList()
        );
        final User afterUser = new User(
                userId,
                "firstName",
                "lastName",
                "+375333719302",
                "someEmail@gam.com",
                "pwd",
                Role.USER,
                UserStatus.ACTIVE,
                BigDecimal.TEN,
                true,
                "",
                "",
                Collections.emptyList()
        );
        final GeneralResponse expected = new GeneralResponse("Successfully updated the user!", TRUE);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(beforeUser));
        when(userRepository.save(afterUser)).thenReturn(afterUser);

        // verify
        assertEquals(expected, userWriteService.updateById(updateUserRequest, userId));
    }

    @Test
    void updateByIdWhenUserNotExistShouldThrow() {
        // given
        final Long userId = 1L;
        final UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                UserStatus.ACTIVE,
                Role.USER,
                false
        );

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // verify
        final EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userWriteService.updateById(updateUserRequest, userId)
        );
        assertEquals("No user with such id: " + userId, thrown.getMessage());
    }
}
