package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.CarDto;
import com.ostapchuk.car.rent.dto.CarsDto;
import com.ostapchuk.car.rent.service.CarService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cars")
public record CarController(CarService carService) {

    @PostMapping
    public void save(@RequestBody final CarDto carDto) {
        carService.save(carDto);
    }

    @GetMapping
    public CarsDto findAll() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    public CarDto findById(@PathVariable final Integer id) {
        return carService.findDtoById(id);
    }

    @PutMapping
    public void update(@RequestBody final CarDto carDto) {
        carService.update(carDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Integer id) {
        carService.delete(id);
    }
}
