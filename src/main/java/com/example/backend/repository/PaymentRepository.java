// src/main/java/com/example/backend/repository/PaymentRepository.java
package com.example.backend.repository;

import com.example.backend.entity.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
 }
