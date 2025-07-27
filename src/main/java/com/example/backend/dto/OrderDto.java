// src/main/java/com/example/backend/dto/OrderDto.java
package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalAmount;
    private Long userId;
    private String userEmail; // Add this field
    private Long sellerId;
    private Long paymentId;
    private List<OrderItemDto> items;
    
    // Custom constructor for existing code that doesn't include userEmail
    public OrderDto(
        Long id,
        LocalDate orderDate,
        String status,
        BigDecimal totalAmount,
        Long userId,
        Long sellerId,
        Long paymentId,
        List<OrderItemDto> items
    ) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.sellerId = sellerId;
        this.paymentId = paymentId;
        this.items = items;
        this.userEmail = null; // Default to null
    }
}