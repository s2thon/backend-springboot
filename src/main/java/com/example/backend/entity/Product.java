package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String image; // opsiyonel

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity_in_stock", nullable = false)
    private Integer stock;

    @Column(name = "product_rate", precision = 3, scale = 2)
    private BigDecimal rate; // opsiyonel

    @Column(name = "review_count")
    private Integer reviewCount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

}
