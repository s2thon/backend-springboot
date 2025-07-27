package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private BigDecimal orderedProductPrice;
    private Integer quantity;
    private String itemStatus;
    private Long productId;
}