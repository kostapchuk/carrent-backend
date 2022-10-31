package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.order.OrderDto;
import com.ostapchuk.car.rent.service.OrderWriteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public record OrderWriteController(OrderWriteService orderWriteService) {

    @PostMapping
    @PreAuthorize("hasAuthority('users:read')")
    public void save(@RequestBody final OrderDto orderDto) {
        orderWriteService.process(orderDto);
    }
}
