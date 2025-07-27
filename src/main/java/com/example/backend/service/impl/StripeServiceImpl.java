package com.example.backend.service.impl;

import com.example.backend.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.SetupIntentCreateParams;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeServiceImpl implements StripeService {

    @Override
    public Customer createCustomer(String email) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .build();
        return Customer.create(params);
    }

    @Override
    public PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setCustomer(customerId) // ◄ Stripe müşteri ID'si eklendi
                .addPaymentMethodType("card")
                .build();
        return PaymentIntent.create(params);
    }
    

    @Override
    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(customerId)
                .build();
        return SetupIntent.create(params);
    }

    @Override
    public List<PaymentMethod> listPaymentMethods(PaymentMethodListParams params) throws StripeException {
        return PaymentMethod.list(params).getData();
    }

    @Override
    public PaymentMethod retrievePaymentMethod(String paymentMethodId) throws StripeException {
        return PaymentMethod.retrieve(paymentMethodId);
    }
}
