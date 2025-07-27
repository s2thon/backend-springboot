package com.example.backend.service;

import com.example.backend.entity.Refund;
import com.example.backend.entity.OrderItem;
import com.example.backend.dto.RefundRequest;
import com.example.backend.dto.RefundDto;

import java.util.List;
import java.util.Optional;

public interface RefundService {
    Refund createRefundRequest(RefundRequest request);
    Refund approveRefund(Long refundId, String adminNote);
    Refund rejectRefund(Long refundId, String adminNote);
    List<Refund> getAllRefundRequests();
    List<Refund> getRefundsByStatus(String status);
    List<Refund> getRefundsByOrderId(Long orderId);
    List<Refund> getRefundsByUserId(Long userId);
    Optional<Refund> getRefundById(Long id);
}