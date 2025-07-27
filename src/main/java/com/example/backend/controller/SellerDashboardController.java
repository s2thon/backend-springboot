package com.example.backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.OrderService;
import com.example.backend.service.ProductService;
import com.example.backend.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

// Backend controller for seller dashboard
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerDashboardController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam Long sellerId) {
        // Validate seller ID
        User seller = userService.findById(sellerId)
            .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        // Get dashboard data
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("sellerName", seller.getFirstName());
        dashboardData.put("totalProducts", productService.countBySellerId(sellerId));
        dashboardData.put("pendingOrders", orderService.findBySellerIdAndStatus(sellerId, "PENDING").size());
        
        // Calculate monthly revenue
        // This is simplified - you'd need to sum order totals from the current month
        dashboardData.put("monthlyRevenue", calculateMonthlyRevenue(sellerId));
        
        return ResponseEntity.ok(dashboardData);
    }
    
    // Helper method to calculate monthly revenue
    private BigDecimal calculateMonthlyRevenue(Long sellerId) {
        // Implementation details...
        return BigDecimal.ZERO;
    }
}