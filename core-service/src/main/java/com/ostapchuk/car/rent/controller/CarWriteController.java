package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.car.CarDto;
import com.ostapchuk.car.rent.service.CarWriteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public record CarWriteController(CarWriteService carWriteService) {

    @PostMapping("/api/v1/cars")
    public void save(@RequestBody final CarDto carDto) {
        carWriteService.save(carDto);
    }

    @PutMapping("/api/v1/cars")
    public void update(@RequestBody final CarDto carDto) {
//        carWriteService.update(carDto);
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/api/v1/cars/{id}")
    public void delete(@PathVariable final Integer id) {
        carWriteService.delete(id);
    }
}
