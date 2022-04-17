package com.ostapchuk.car.rent.service;

import com.ostapchuk.car.rent.exception.PayPalException;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;
    private final UserService userService;

    public String createOrder(final Long userId, final String currency, final String method, final String intent,
                              final String description, final String cancelUrl, final String successUrl) {
        final Payment payment = createPayment(intent, createPayer(method),
                List.of(createTransaction(description, userId, currency)),
                createRedirectUrls(cancelUrl, successUrl));
        return payment.getLinks()
                .stream()
                .filter(l -> l.getRel().equals("approval_url"))
                .map(Links::getHref).findFirst()
                .orElseThrow(() -> new PayPalException("Could not proceed with the payment. Please, try again later"));
    }

    @SneakyThrows({PayPalRESTException.class})
    public void executePayment(final String paymentId, final String payerId) {
        final Payment payment = new Payment();
        payment.setId(paymentId);
        final PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        payment.execute(apiContext, paymentExecute);
    }

    @SneakyThrows({PayPalRESTException.class})
    private Payment createPayment(final String intent, final Payer payer, final List<Transaction> transactions,
                                  final RedirectUrls redirectUrls) {
        final Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);
        return payment.create(apiContext);
    }

    private RedirectUrls createRedirectUrls(final String cancelUrl, final String successUrl) {
        final RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        return redirectUrls;
    }

    private Payer createPayer(final String method) {
        final Payer payer = new Payer();
        payer.setPaymentMethod(method);
        return payer;
    }

    private Transaction createTransaction(final String description, final Long userId, final String currency) {
        final Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(createAmount(userId, currency));
        return transaction;
    }

    private Amount createAmount(final Long userId, final String currency) {
        final Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(userService.findDept(userId).toString());
        return amount;
    }
}