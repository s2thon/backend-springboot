package com.example.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundDto {
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private String productName;
    private BigDecimal amount;
    private String reason;
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime processDate;
    private String adminNote;
    private String userName;
}