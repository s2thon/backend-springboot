// src/main/java/com/example/backend/dto/OrderRequest.java
package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long userId;
    private Long paymentId;
    private List<OrderItemDto> items;
}