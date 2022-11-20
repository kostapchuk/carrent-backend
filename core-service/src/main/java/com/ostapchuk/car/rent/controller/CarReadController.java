package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.car.CarResponse;
import com.ostapchuk.car.rent.service.CarReadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public record CarReadController(CarReadService carReadService) {

    @GetMapping("/api/v1/cars/free")
    public List<CarResponse> findAllFree(
            @RequestParam(value = "userId", required = false) final Optional<Long> userId) {
        return userId.isPresent()
                ? carReadService.findAllFreeForUser(userId.get())
                : carReadService.findAllFree();
    }

    @GetMapping("/api/v1/cars/{id}")
    public CarResponse findById(@PathVariable final Integer id) {
        return carReadService.findById(id);
    }
}
