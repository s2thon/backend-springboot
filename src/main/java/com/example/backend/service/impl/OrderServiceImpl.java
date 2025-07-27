// src/main/java/com/example/backend/service/impl/OrderServiceImpl.java
package com.example.backend.service.impl;

import com.example.backend.entity.Order;
import com.example.backend.entity.Refund;
import com.example.backend.repository.OrderRepository;
import com.example.backend.service.OrderService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional // Tüm servis metodlarını transactional yap
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order save(Order order) {
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
            throw e; // Hatayı yukarıya ilet
        }
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findBySellerId(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Order> findBySellerIdAndStatus(Long sellerId, String status) {
        return orderRepository.findBySellerIdAndStatus(sellerId, status);
    }

    @Override
    public long countPendingOrdersBySellerId(Long sellerId) {
        return orderRepository.findBySellerIdAndStatus(sellerId, "PENDING_APPROVAL").size();
    }

    @Override
    public BigDecimal getMonthlyRevenueBySellerId(Long sellerId, LocalDate startDate, LocalDate endDate) {
        // Satıcının onaylanan siparişlerini getir
        List<Order> approvedOrders = orderRepository.findBySellerIdAndStatusAndOrderDateBetween(
            sellerId, 
            "APPROVED", 
            startDate, 
            endDate
        );

        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        for (Order order : approvedOrders) {
            if (order.getTotalAmount() != null) {
                // İade edilmiş ürünleri hesapla
                BigDecimal refundAmount = BigDecimal.ZERO;
                for (Refund refund : order.getRefunds()) {
                    if ("APPROVED".equals(refund.getStatus()) || "COMPLETED".equals(refund.getStatus())) {
                        refundAmount = refundAmount.add(refund.getAmount());
                    }
                }
                
                // Toplam tutardan iade tutarını çıkar
                BigDecimal orderRevenue = order.getTotalAmount().subtract(refundAmount);
                totalRevenue = totalRevenue.add(orderRevenue);
            }
        }

        return totalRevenue;
    }
}
