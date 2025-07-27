// src/main/java/com/example/backend/repository/OrderRepository.java
package com.example.backend.repository;

import com.example.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findBySellerIdAndStatus(Long sellerId, String status);
    
    // YENİ: Tarih aralığı için onaylanmış siparişleri getir
    List<Order> findBySellerIdAndStatusAndOrderDateBetween(
        Long sellerId, 
        String status, 
        LocalDate startDate, 
        LocalDate endDate
    );
}