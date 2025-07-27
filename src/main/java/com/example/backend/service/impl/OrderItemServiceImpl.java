// src/main/java/com/example/backend/service/impl/OrderItemServiceImpl.java
package com.example.backend.service.impl;

import com.example.backend.entity.OrderItem;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository repo;

    @Override
    public List<OrderItem> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public OrderItem save(OrderItem item) {
        return repo.save(item);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
