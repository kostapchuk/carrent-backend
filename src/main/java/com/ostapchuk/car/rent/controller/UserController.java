package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.RegisterUserDto;
import com.ostapchuk.car.rent.dto.RequestPaymentDto;
import com.ostapchuk.car.rent.dto.ResultDto;
import com.ostapchuk.car.rent.dto.RidesDto;
import com.ostapchuk.car.rent.dto.RolesDto;
import com.ostapchuk.car.rent.dto.StatusesDto;
import com.ostapchuk.car.rent.dto.UserDto;
import com.ostapchuk.car.rent.dto.UsersDto;
import com.ostapchuk.car.rent.service.OrderService;
import com.ostapchuk.car.rent.service.PaypalService;
import com.ostapchuk.car.rent.service.UserService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final PaypalService paypalService;

    // TODO: 3/18/2022 check the same user
    @GetMapping("/{id}/rides")
    @PreAuthorize("hasAuthority('users:read')")
    public RidesDto findAllRidesById(@PathVariable final Long id) {
        return orderService.findAllRidesByUserId(id);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAuthority('users:read')")
    public BigDecimal findBalanceById(@PathVariable final Long id) {
        return userService.findBalanceById(id);
    }

    @GetMapping
    public UsersDto findAll() {
        return userService.findAll();
    }

    @PostMapping
    public ResultDto register(@RequestBody final RegisterUserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping
    public ResultDto update(@RequestBody final UserDto userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('users:delete')")
    public void delete(@PathVariable final Long id) {
        userService.deleteById(id);
    }

    @SneakyThrows
    @PostMapping("/pay")
    @PreAuthorize("hasAuthority('users:read')")
    public String payTheDebt(@RequestBody final RequestPaymentDto paymentDto, final HttpServletResponse response) {
        final Payment payment = paypalService.createPayment(paymentDto.userId(), "USD", "paypal",
                "sale", "standard description", "http://localhost:3000/cancelled-payment",
                "http://localhost:3000/success-payment");
        for (final Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return link.getHref();
            }
        }
        return "";
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasAuthority('users:read')")
    public boolean cancel(@RequestParam("paymentId") final String paymentId, @RequestParam("PayerID") final String payerId) {
        final Payment payment = paypalService.executePayment(paymentId, payerId);
        //            userService.updateBalanceById();
        return payment.getState().equals("approved");
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('users:write')")
    public RolesDto findRoles() {
        return userService.findAllRoles();
    }

    @GetMapping("/statuses")
    @PreAuthorize("hasAuthority('users:write')")
    public StatusesDto findStatuses() {
        return userService.findAllStatuses();
    }

    @GetMapping("/success")
    @PreAuthorize("hasAuthority('users:read')")
    public String success(@RequestParam("paymentId") final String paymentId, @RequestParam("PayerID") final String payerId) {
        final Payment payment = paypalService.executePayment(paymentId, payerId);
        if (payment.getState().equals("approved")) {
//            userService.updateBalanceById();
            return "success";
        } else {
            return "Not approved";
        }
    }
}
