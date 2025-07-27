package com.example.backend.controller;

import com.example.backend.service.UserService;
import com.example.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin") // Sadece admin path'i
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')") // Admin rolü için koruma ekle
public class DashboardController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/dashboard") // Admin dashboard endpoint'i - /api/admin/dashboard
    public ResponseEntity<?> getDashboardData() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalUsers", userService.countAllUsers());
            dashboardData.put("totalSellers", userService.countSellers());
            dashboardData.put("pendingSellerRequests", userService.countPendingSellerRequests());
            dashboardData.put("totalProducts", productService.countAllProducts());
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard data: " + e.getMessage());
        }
    }
}