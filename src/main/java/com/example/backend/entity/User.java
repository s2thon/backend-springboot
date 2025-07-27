package com.example.backend.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "stripe_customer_id", length = 255)
    private String stripeCustomerId;

    @Column(name = "auth_provider", length = 50)
    private String authProvider; // e.g. "google", "local"

    @Column(name = "provider_id", length = 255)
    private String providerId; // OAuth provider’dan gelen ID

    @Column(name = "seller_requested", nullable = false)
    private Boolean sellerRequested = false;

    @Column(name = "seller_approved", nullable = false)
    private Boolean sellerApproved = false;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "seller")
    private List<Product> products;
}
