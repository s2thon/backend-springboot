package com.example.backend.controller;

import com.example.backend.dto.OrderDto;
import com.example.backend.entity.Order;
import com.example.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Controller seviyesinde koruma
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        
        // OrderDto dönüşümü
        List<OrderDto> orderDtos = orders.stream()
            .map(order -> {
                OrderDto dto = new OrderDto();
                dto.setId(order.getId());
                dto.setOrderDate(order.getOrderDate());
                dto.setStatus(order.getStatus());
                dto.setTotalAmount(order.getTotalAmount());
                
                // Kullanıcı bilgilerini ekle
                if (order.getUser() != null) {
                    dto.setUserEmail(order.getUser().getEmail());
                    dto.setUserId(order.getUser().getId());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(orderDtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
            .map(order -> {
                OrderDto dto = new OrderDto();
                // DTO dönüşüm işlemleri...
                dto.setId(order.getId());
                dto.setOrderDate(order.getOrderDate());
                dto.setStatus(order.getStatus());
                dto.setTotalAmount(order.getTotalAmount());
                
                if (order.getUser() != null) {
                    dto.setUserEmail(order.getUser().getEmail());
                    dto.setUserId(order.getUser().getId());
                }
                
                return ResponseEntity.ok(dto);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}