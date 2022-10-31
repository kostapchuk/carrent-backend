package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.car.CarDto;
import com.ostapchuk.car.rent.service.CarReadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public record CarReadController(CarReadService carReadService) {

    @GetMapping("/api/v1/cars")
    public List<CarDto> findAll() {
        return carReadService.findAll();
    }

    @GetMapping("/api/v1/cars/free")
    public List<CarDto> findAllFree(@RequestParam(value = "userId", required = false) final Optional<Long> userId) {
        if (userId.isPresent()) {
            return carReadService.findAllFreeForUser(userId.get());
        } else {
            return carReadService.findAllFree();
        }
    }

    @GetMapping("/api/v1/cars/{id}")
//    @PreAuthorize("hasAuthority('users:read')")
    public CarDto findById(@PathVariable final Integer id) {
        return carReadService.findDtoById(id);
    }
}
