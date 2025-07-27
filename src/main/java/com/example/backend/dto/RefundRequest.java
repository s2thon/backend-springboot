package com.example.backend.dto;

import lombok.Data;

@Data
public class RefundRequest {
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private String reason;
}