package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.service.UserWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class UserWriteController {

    private final UserWriteService userWriteService;

    @PostMapping("/api/v1/users")
    public ResultDto register(@RequestBody final RegisterUserDto userDto) {
        return userWriteService.create(userDto);
    }

    @PutMapping("/api/v1/users")
    public ResultDto update(@RequestBody final UserDto userDto) {
//        return userWriteService.update(userDto);
        return new ResultDto("Not implemented", false);
    }

    @DeleteMapping("/api/v1/users/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public void delete(@PathVariable final Long id) {
        userWriteService.deleteById(id);
    }

    @PostMapping("/api/v1/users/{id}/pay")
    @PreAuthorize("hasAuthority('users:read')")
    public void payDebt(@PathVariable final Long id) {
        userWriteService.payDebt(id);
    }

    @PatchMapping(path = "/api/v1/users/{id}/passport", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('users:read')")
    public CompletableFuture<ResultDto> uploadPassport(@RequestPart("file") final MultipartFile file,
                                                       @PathVariable("id") final Long userId) {
        return userWriteService.updatePassportDocument(file, userId);
    }

    @PatchMapping(path = "/api/v1/users/{id}/driving_license", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('users:read')")
    public CompletableFuture<ResultDto> uploadDrivingLicense(@RequestPart("file") final MultipartFile file,
                                                             @PathVariable("id") final Long userId) {
        return userWriteService.updateDrivingLicenseDocument(file, userId);
    }
}
