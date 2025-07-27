// src/main/java/com/example/backend/service/OrderItemService.java
package com.example.backend.service;

import com.example.backend.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    List<OrderItem> findAll();
    Optional<OrderItem> findById(Long id);
    OrderItem save(OrderItem item);
    void deleteById(Long id);
}
