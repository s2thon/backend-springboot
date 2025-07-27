// src/main/java/com/example/backend/service/impl/PaymentServiceImpl.java
package com.example.backend.service.impl;

import com.example.backend.entity.Payment;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;

    @Override
    public List<Payment> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Payment save(Payment payment) {
        return repo.save(payment);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
}
