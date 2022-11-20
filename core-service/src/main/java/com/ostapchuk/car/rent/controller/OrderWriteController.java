package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.order.OrderRequest;
import com.ostapchuk.car.rent.processor.StartingRideStatusProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderWriteController {

    private final StartingRideStatusProcessor startingRideStatusProcessor;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.ostapchuk.car.rent.entity.Permission).USERS_READ.getName())")
    public void save(@RequestBody final OrderRequest orderRequest) {
        startingRideStatusProcessor.process(orderRequest);
    }
}
