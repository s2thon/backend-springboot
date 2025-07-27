package com.example.backend.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long amount;
    private String currency;
    private String customerId;
}