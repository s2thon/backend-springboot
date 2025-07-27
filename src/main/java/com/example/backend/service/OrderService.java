// src/main/java/com/example/backend/service/OrderService.java
package com.example.backend.service;

import com.example.backend.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    Order save(Order order);
    void deleteById(Long id);

    // Eklenenler:
    List<Order> findByUserId(Long userId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findBySellerIdAndStatus(Long sellerId, String status);
    
    // YENİ: Bekleyen siparişlerin sayısını getir
    long countPendingOrdersBySellerId(Long sellerId);
    
    // YENİ: Aylık geliri hesapla
    BigDecimal getMonthlyRevenueBySellerId(Long sellerId, LocalDate startDate, LocalDate endDate);
}