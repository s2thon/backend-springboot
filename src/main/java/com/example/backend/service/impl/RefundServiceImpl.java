package com.example.backend.service.impl;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.service.RefundService;
import com.example.backend.service.OrderService;
import com.example.backend.dto.RefundRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public RefundServiceImpl(
            RefundRepository refundRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Refund createRefundRequest(RefundRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Sipariş bulunamadı"));
        
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("Sipariş kalemi bulunamadı"));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));
        
        // Zaten iade talep edilmiş mi kontrol et
        boolean alreadyRequested = refundRepository.findByOrderItemId(orderItem.getId())
                .stream()
                .anyMatch(r -> !r.getStatus().equals("REJECTED"));
        
        if (alreadyRequested) {
            throw new IllegalStateException("Bu ürün için zaten bir iade talebi bulunmaktadır.");
        }
        
        // OrderItem'ın refundStatus'ını güncelle
        orderItem.setRefundStatus("REQUESTED");
        orderItemRepository.save(orderItem);
        
        // Yeni iade talebi oluştur
        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setOrderItem(orderItem);
        refund.setUser(user);
        refund.setAmount(orderItem.getOrderedProductPrice().multiply(new java.math.BigDecimal(orderItem.getQuantity())));
        refund.setReason(request.getReason());
        refund.setStatus("REQUESTED");
        refund.setRequestDate(LocalDateTime.now());
        
        return refundRepository.save(refund);
    }

    @Override
    public Refund approveRefund(Long refundId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("İade talebi bulunamadı"));
        
        refund.setStatus("APPROVED");
        refund.setProcessDate(LocalDateTime.now());
        refund.setAdminNote(adminNote);
        
        // OrderItem'ın refundStatus'ını güncelle
        OrderItem orderItem = refund.getOrderItem();
        orderItem.setRefundStatus("APPROVED");
        orderItemRepository.save(orderItem);
        
        // Order'ın status'ını güncelle
        Order order = refund.getOrder();
        
        // Siparişin tüm kalemleri iade edildi mi kontrol et
        boolean allItemsRefunded = order.getItems().stream()
                .allMatch(item -> "APPROVED".equals(item.getRefundStatus()) || "COMPLETED".equals(item.getRefundStatus()));
        
        // Eğer tüm ürünler iade edildiyse siparişi tamamen iade edildi olarak işaretle
        if (allItemsRefunded) {
            order.setStatus("REFUNDED");
        } else {
            // Aksi takdirde kısmen iade edildi
            order.setStatus("PARTIALLY_REFUNDED");
        }
        orderRepository.save(order);
        
        return refundRepository.save(refund);
    }

    @Override
    public Refund rejectRefund(Long refundId, String adminNote) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("İade talebi bulunamadı"));
        
        refund.setStatus("REJECTED");
        refund.setProcessDate(LocalDateTime.now());
        refund.setAdminNote(adminNote);
        
        // OrderItem'ın refundStatus'ını güncelle
        OrderItem orderItem = refund.getOrderItem();
        orderItem.setRefundStatus("REJECTED");
        orderItemRepository.save(orderItem);
        
        return refundRepository.save(refund);
    }

    @Override
    public List<Refund> getAllRefundRequests() {
        return refundRepository.findAll();
    }

    @Override
    public List<Refund> getRefundsByStatus(String status) {
        return refundRepository.findByStatus(status);
    }

    @Override
    public List<Refund> getRefundsByOrderId(Long orderId) {
        return refundRepository.findByOrderId(orderId);
    }

    @Override
    public List<Refund> getRefundsByUserId(Long userId) {
        return refundRepository.findByUserId(userId);
    }

    @Override
    public Optional<Refund> getRefundById(Long id) {
        return refundRepository.findById(id);
    }
}