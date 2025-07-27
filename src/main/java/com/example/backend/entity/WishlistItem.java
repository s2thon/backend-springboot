// src/main/java/com/example/backend/entity/WishlistItem.java
package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist_item")
@Data               // @Getter, @Setter, equals/hashCode, toString vs. içerir
@NoArgsConstructor  // parametresiz ctor
@AllArgsConstructor // tüm alanları alan ctor
@Builder
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;      // ← buraya Lombok ile setUser(User user) eklenir

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // ← buraya Lombok ile setProduct(Product product) eklenir
}
