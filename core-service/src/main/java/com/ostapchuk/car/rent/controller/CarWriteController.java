package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.CarDto;
import com.ostapchuk.car.rent.service.CarWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CarWriteController {

    private final CarWriteService carWriteService;

    @PostMapping("/api/v1/cars")
    @PreAuthorize("hasAuthority('users:write')")
    public void save(@RequestBody final CarDto carDto) {
        carWriteService.save(carDto);
    }

    @PutMapping("/api/v1/cars")
    @PreAuthorize("hasAuthority('users:write')")
    public void update(@RequestBody final CarDto carDto) {
        carWriteService.update(carDto);
    }

    @DeleteMapping("/api/v1/cars/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public void delete(@PathVariable final Integer id) {
        carWriteService.delete(id);
    }
}
