package com.example.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.backend.service.StripeService;
import com.stripe.exception.StripeException;
import com.example.backend.dto.CheckoutRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class CheckoutController {

    private final StripeService stripeService;

    @PostMapping("/checkout")
public ResponseEntity<Map<String, String>> checkout(@RequestBody CheckoutRequest request) throws StripeException {
    String clientSecret = stripeService.createPaymentIntent(
        request.getAmount(),
        request.getCurrency(),
        request.getCustomerId()
    ).getClientSecret();

    return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
}


}
