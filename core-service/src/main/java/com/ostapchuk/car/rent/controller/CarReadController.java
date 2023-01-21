package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.car.CarResponse;
import com.ostapchuk.car.rent.service.CarReadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public record CarReadController(CarReadService carReadService) {

    @GetMapping("/api/v1/cars/free")
    public List<CarResponse> findAllFree() {
        return carReadService.findAllFree();
    }

    @GetMapping("/api/v1/cars/available")
    public List<CarResponse> findAllAvailableForUser(@RequestParam final Long userId) {
        return carReadService.findAllFreeForUser(userId);
    }

    @GetMapping("/api/v1/cars/{id}")
    public CarResponse findById(@PathVariable final Integer id) {
        return carReadService.findById(id);
    }
}
