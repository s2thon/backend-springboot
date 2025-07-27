// src/main/java/com/example/backend/service/PaymentService.java
package com.example.backend.service;

import com.example.backend.entity.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> findAll();
    Optional<Payment> findById(Long id);
    Payment save(Payment payment);
    void deleteById(Long id);

    List<Payment> findByUserId(Long userId);
}
