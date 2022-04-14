package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.RequestPaymentDto;
import com.ostapchuk.car.rent.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    public static final String USD = "USD";
    public static final String PAYPAL = "paypal";
    public static final String SALE = "sale";
    public static final String STANDARD_DESCRIPTION = "standard description";
    public static final String PAYMENTS_CANCEL = "http://localhost:8080/api/v1/payments/cancel";
    public static final String PAYMENTS_SUCCESS = "http://localhost:8080/api/v1/payments/success";
    private final PaypalService paypalService;

    @SneakyThrows
    @PostMapping("/pay")
    public String payTheDebt(@RequestBody final RequestPaymentDto paymentDto) {
        return paypalService.createOrder(paymentDto.userId(), USD, PAYPAL, SALE, STANDARD_DESCRIPTION, PAYMENTS_CANCEL,
                PAYMENTS_SUCCESS);
    }

    @GetMapping("/success")
    public void success(@RequestParam("paymentId") final String paymentId,
                        @RequestParam("PayerID") final String payerId) {
        paypalService.executePayment(paymentId, payerId);
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Cancelled";
    }
}
