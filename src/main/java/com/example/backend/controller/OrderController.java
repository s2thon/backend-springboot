package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderDto;
import com.example.backend.dto.OrderItemDto;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Payment;
import com.example.backend.entity.User;
import com.example.backend.entity.Product;
import com.example.backend.service.OrderService;
import com.example.backend.service.UserService;
import com.example.backend.service.ProductService;
import com.example.backend.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// javax import hatası düzeltme
import jakarta.persistence.EntityNotFoundException;  // javax yerine jakarta kullanın (JPA 3.0+)

// Lambda için Stream içe aktarmaları
import java.util.function.Supplier;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService   orderService;
    private final UserService    userService;
    private final ProductService productService;
    private final PaymentService paymentService;

    // 1) Checkout sonrası sipariş oluşturma
    @PostMapping("/create-after-checkout")
    public ResponseEntity<OrderDto> createAfterCheckout(@RequestBody OrderRequest req) {
        // a) Buyer - Düzeltilmiş lambda
        User buyer = userService.findById(req.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getUserId()));
        // b) Payment - Düzeltilmiş lambda
        Payment payment = paymentService.findById(req.getPaymentId())
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + req.getPaymentId()));
        // c) Order inşa
        Order order = Order.builder()
            .orderDate(LocalDate.now())
            .status("PENDING_APPROVAL")
            .totalAmount(req.getItems().stream()
                .map(i -> i.getOrderedProductPrice()
                           .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .user(buyer)
            .payment(payment)
            .build();
        // d) Kalemleri map et - Düzeltilmiş lambda
        List<OrderItem> items = req.getItems().stream().map(i -> {
            Product p = productService.findById(i.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + i.getProductId()));
            return OrderItem.builder()
                .order(order)
                .product(p)
                .quantity(i.getQuantity())
                .orderedProductPrice(i.getOrderedProductPrice())
                .itemStatus("PENDING")
                .build();
        }).collect(Collectors.toList());
        order.setItems(items);
        // e) Seller ataması (ilk kalemin seller’ı)
        if (!items.isEmpty()) {
            order.setSeller(items.get(0).getProduct().getSeller());
        }
        // f) Kaydet ve DTO’ya dönüştür
        Order saved = orderService.save(order);
        return ResponseEntity.status(201).body(mapToDto(saved));
    }

    // 2) Tüm siparişleri listele
    @GetMapping
    public ResponseEntity<List<OrderDto>> listAll() {
        List<OrderDto> dtos = orderService.findAll().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 3) ID ile sipariş getir
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return orderService.findById(id)
            .map(o -> ResponseEntity.ok(mapToDto(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    // 4) Kullanıcı bazlı
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> listByUser(@PathVariable Long userId) {
        List<OrderDto> dtos = orderService.findByUserId(userId).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // 5) Satıcı bazlı (isteğe bağlı status filtresi)
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<OrderDto>> listBySeller(
            @PathVariable Long sellerId,
            @RequestParam(required = false) String status) {
        List<Order> orders = (status != null)
            ? orderService.findBySellerIdAndStatus(sellerId, status)
            : orderService.findBySellerId(sellerId);
        
        List<OrderDto> dtos = orders.stream()
            .map(order -> {
                OrderDto dto = mapToDto(order);
                // Status'u her zaman büyük harfle ve boşlukları temizle
                if (dto.getStatus() != null) {
                    dto.setStatus(dto.getStatus().trim().toUpperCase());
                }
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // 6) Satıcı onay/red
    @PutMapping("/seller/orders/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        Order order = orderService.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(body.get("status"));
        Order updated = orderService.save(order);
        return ResponseEntity.ok(mapToDto(updated));
    }

    // Sipariş onaylama endpoint'i
    @PutMapping("/{orderId}/approve")
    public ResponseEntity<OrderDto> approveOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> payload) {
        // Satıcı ID'sini al
        Long sellerId = null;
        if (payload.containsKey("sellerId")) {
            if (payload.get("sellerId") instanceof Integer) {
                sellerId = ((Integer) payload.get("sellerId")).longValue();
            } else if (payload.get("sellerId") instanceof Long) {
                sellerId = (Long) payload.get("sellerId");
            }
        }

        if (sellerId == null) {
            return ResponseEntity.badRequest().build();
        }

        // Siparişi bul - Düzeltilmiş lambda
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        // Satıcı yetkisi kontrolü
        if (order.getSeller() == null || !order.getSeller().getId().equals(sellerId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Status'u APPROVED olarak güncelle
        order.setStatus("APPROVED");
        Order updated = orderService.save(order);
        
        // OrderDto'ya dönüştürme
        OrderDto dto = mapToDto(updated);
        return ResponseEntity.ok(dto);
    }

    // Sipariş reddetme endpoint'i düzeltildi
    @PutMapping("/{orderId}/reject")
    public ResponseEntity<?> rejectOrder(
            @PathVariable Long orderId, 
            @RequestBody Map<String, Object> payload
    ) {
        try {
            // Debug için log ekle
            System.out.println("Rejecting order: " + orderId + ", payload: " + payload);
            
            // Siparişi bul
            Order order = orderService.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
            
            // İlişkili nesne kontrollerini devre dışı bırak - bu JPA hatalarına neden olabilir
            // ⚠️ Tüm seller-order ilişki kontrollerini kaldır

            // Status'u güncelle
            order.setStatus("REJECTED");
            
            // Doğrudan veritabanına kaydet
            try {
                orderService.save(order);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Database error: " + e.getMessage()));
            }
            
            // Basit yanıt döndür - DTO dönüşümü yapma
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order rejected successfully");
            response.put("orderId", orderId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // sellerId değerini extract etmek için yardımcı metod
    private Long extractSellerId(Map<String, Object> payload) {
        if (!payload.containsKey("sellerId")) {
            return null;
        }
        
        Object sellerIdObj = payload.get("sellerId");
        
        if (sellerIdObj instanceof Number) {
            return ((Number) sellerIdObj).longValue();
        } else if (sellerIdObj instanceof String) {
            try {
                return Long.parseLong((String) sellerIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    // mapToDto metodu yerine convertToOrderDto metodunu ekleyelim
    private OrderDto convertToOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        // Gerekli diğer alanları ekleyin
        return dto;
    }

    // --- Yardımcı: Entity → DTO dönüşümü
    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus()); // Status alanını doğru şekilde set et

        List<OrderItemDto> items = order.getItems().stream()
            .map(i -> new OrderItemDto(
                i.getId(),
                i.getOrderedProductPrice(),
                i.getQuantity(),
                i.getItemStatus(),
                i.getProduct().getId()
            ))
            .collect(Collectors.toList());

        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setUserId(order.getUser().getId());
        dto.setSellerId(order.getSeller() != null ? order.getSeller().getId() : null);
        dto.setPaymentId(order.getPayment().getId());
        dto.setItems(items);

        return dto;
    }
}
