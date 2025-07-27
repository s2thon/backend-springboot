package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    // “PENDING_APPROVAL” da geçerli olsun:
    @Pattern(regexp = "PENDING|PENDING_APPROVAL|APPROVED|SHIPPED|CANCELLED|REFUNDED|PARTIALLY_REFUNDED", 
             message = "Invalid order status")
    @Column(name = "order_status", length = 50, nullable = false)
    private String status;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // ★ Ödeme ilişkilendirme
    @OneToOne(optional = false)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private User seller;

    // Order sınıfına refund ilişkisini ekleyin
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Refund> refunds;
}
