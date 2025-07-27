// src/main/java/com/example/backend/entity/OrderItem.java
package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "ordered_product_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal orderedProductPrice;

    @Column(name = "quantity_in_order", nullable = false)
    private Integer quantity;

    @Column(name = "item_status", length = 50)
    private String itemStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<Refund> refunds;

    @Column(name = "refund_status", length = 50)
    private String refundStatus; // NONE, REQUESTED, APPROVED, REJECTED, COMPLETED
}
