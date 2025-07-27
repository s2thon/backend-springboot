package com.example.backend.service;

import java.util.List;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentMethodListParams;

public interface StripeService {
    Customer createCustomer(String email) throws StripeException;
    PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException;

     // ◄ Kart kaydetme (SetupIntent) için
    SetupIntent createSetupIntent(String customerId) throws StripeException;

    // ◄ Kayıtlı kartları listelemek için
    List<PaymentMethod> listPaymentMethods(PaymentMethodListParams params) throws StripeException;

    // ◄ Tek bir kart bilgisini çekmek için
    PaymentMethod retrievePaymentMethod(String paymentMethodId) throws StripeException;
}
