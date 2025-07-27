package com.example.backend.repository;

import com.example.backend.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByOrderId(Long orderId);
    List<Refund> findByUserId(Long userId);
    List<Refund> findByStatus(String status);
    List<Refund> findByOrderItemId(Long orderItemId);
}