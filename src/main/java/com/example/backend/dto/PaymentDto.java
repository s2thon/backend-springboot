// src/main/java/com/example/backend/dto/PaymentDto.java
package com.example.backend.dto;

import java.math.BigDecimal;

public record PaymentDto(
    Long id,
    String paymentMethod,
    String stripeCustomerId,
    String stripePaymentIntentId,
    String cardLastFour,
    String cardBrand,
    Integer cardExpirationMonth,
    Integer cardExpirationYear,
    BigDecimal amount,
    String currency,
    String status,
    Long userId
) {}
