package com.example.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String reason;
    
    // REQUESTED, APPROVED, REJECTED, COMPLETED
    @Column(nullable = false, length = 50)
    private String status;
    
    @Column(nullable = false)
    private LocalDateTime requestDate;
    
    private LocalDateTime processDate;
    
    @Column(length = 500)
    private String adminNote;
}