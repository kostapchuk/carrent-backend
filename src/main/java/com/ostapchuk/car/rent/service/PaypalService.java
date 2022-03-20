package com.ostapchuk.car.rent.service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;

    @SneakyThrows({PayPalRESTException.class})
    public Payment createPayment(final BigDecimal total, final String currency, final String method, final String intent,
                                 final String description, final String cancelUrl, final String successUrl) {
        final Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total.toString());
        final Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        final List transactions = new ArrayList<>();
        transactions.add(transaction);
        final Payer payer = new Payer();
        payer.setPaymentMethod(method);
        final Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        final RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        return payment.create(apiContext);
    }

    @SneakyThrows({PayPalRESTException.class})
    public Payment executePayment(final String paymentId, final String payerId) {
        final Payment payment = new Payment();
        payment.setId(paymentId);
        final PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }
}