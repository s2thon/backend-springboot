package com.example.backend.controller;

import com.example.backend.dto.RefundDto;
import com.example.backend.dto.RefundRequest;
import com.example.backend.entity.Refund;
import com.example.backend.entity.OrderItem;
import com.example.backend.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    @Autowired
    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    // Müşteri tarafından iade talebi oluşturma
    @PostMapping("/request")
    public ResponseEntity<?> createRefundRequest(@RequestBody RefundRequest request) {
        try {
            Refund refund = refundService.createRefundRequest(request);
            return ResponseEntity.ok(convertToDto(refund));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Admin tarafından iade talebini onaylama
    @PutMapping("/{refundId}/approve")
    public ResponseEntity<?> approveRefund(
            @PathVariable Long refundId,
            @RequestBody Map<String, String> payload) {
        try {
            String adminNote = payload.getOrDefault("adminNote", "");
            Refund refund = refundService.approveRefund(refundId, adminNote);
            return ResponseEntity.ok(convertToDto(refund));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Admin tarafından iade talebini reddetme
    @PutMapping("/{refundId}/reject")
    public ResponseEntity<?> rejectRefund(
            @PathVariable Long refundId,
            @RequestBody Map<String, String> payload) {
        try {
            String adminNote = payload.getOrDefault("adminNote", "");
            Refund refund = refundService.rejectRefund(refundId, adminNote);
            return ResponseEntity.ok(convertToDto(refund));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Tüm iade taleplerini listele (admin)
    @GetMapping
    public ResponseEntity<List<RefundDto>> getAllRefunds() {
        List<Refund> refunds = refundService.getAllRefundRequests();
        List<RefundDto> refundDtos = refunds.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(refundDtos);
    }

    // Duruma göre iade taleplerini listele (admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RefundDto>> getRefundsByStatus(@PathVariable String status) {
        List<Refund> refunds = refundService.getRefundsByStatus(status);
        List<RefundDto> refundDtos = refunds.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(refundDtos);
    }

    // Belirli bir kullanıcının iade taleplerini listele
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RefundDto>> getRefundsByUser(@PathVariable Long userId) {
        List<Refund> refunds = refundService.getRefundsByUserId(userId);
        List<RefundDto> refundDtos = refunds.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(refundDtos);
    }

    // Belirli bir siparişin iade taleplerini listele
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<RefundDto>> getRefundsByOrder(@PathVariable Long orderId) {
        List<Refund> refunds = refundService.getRefundsByOrderId(orderId);
        List<RefundDto> refundDtos = refunds.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(refundDtos);
    }

    // Yardımcı method - Refund entity'sini DTO'ya dönüştürme
    private RefundDto convertToDto(Refund refund) {
        RefundDto dto = new RefundDto();
        dto.setId(refund.getId());
        dto.setOrderId(refund.getOrder().getId());
        dto.setOrderItemId(refund.getOrderItem().getId());
        
        OrderItem item = refund.getOrderItem();
        dto.setProductName(item.getProduct().getName());
        
        dto.setAmount(refund.getAmount());
        dto.setReason(refund.getReason());
        dto.setStatus(refund.getStatus());
        dto.setRequestDate(refund.getRequestDate());
        dto.setProcessDate(refund.getProcessDate());
        dto.setAdminNote(refund.getAdminNote());
        dto.setUserName(refund.getUser().getFirstName() + " " + refund.getUser().getLastName());
        
        return dto;
    }
}