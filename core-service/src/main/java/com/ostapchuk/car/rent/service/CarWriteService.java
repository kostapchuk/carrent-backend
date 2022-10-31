package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.dto.car.CarDto;
import com.ostapchuk.car.rent.mapper.CarMapper;
import com.ostapchuk.car.rent.repository.CarRepository;
import org.springframework.stereotype.Service;

@Service
public record CarWriteService(CarRepository carRepository,
                              CarMapper carMapper) {

    public void save(final CarDto carDto) {
        carRepository.save(carMapper.toEntity(carDto));
    }

//    public void update(final CarDto carDto) {
//        final Integer id = carDto.id();
//        final Car car = carReadService.findById(id);
//        car.setMark(carDto.mark());
//        car.setModel(carDto.model());
//        car.setBookPricePerHour(carDto.bookPricePerHour());
//        car.setRentPricePerHour(carDto.rentPricePerHour());
//        car.setImgLink(carDto.imgUrl());
//        carRepository.save(car);
//    }

    public void delete(final Integer id) {
        carRepository.deleteById(id);
    }
}
