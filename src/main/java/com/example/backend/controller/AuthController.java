package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.StripeService;
import com.example.backend.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StripeService stripeService;  // ⭐ Stripe servis eklendi

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().trim().isEmpty() ||
            req.getPassword() == null || req.getPassword().trim().isEmpty() ||
            req.getFirstName() == null || req.getFirstName().trim().isEmpty() ||
            req.getLastName() == null || req.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        if (userService.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = new User();
        user.setEmail(req.getEmail().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword().trim()));
        user.setFirstName(req.getFirstName().trim());
        user.setLastName(req.getLastName().trim());
        user.setRole(userRole);
        user.setActive(true);
        user.setSellerRequested(Boolean.TRUE.equals(req.getSellerRequested()));
        user.setSellerApproved(false);

        try {
            userService.save(user);
            // İkinci parametre olarak user.getActive() değerini de geçirelim
            String token = jwtUtil.generateToken(user.getEmail(), user.getActive());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // Kullanıcıyı bul
            User user = userService.findByEmail(req.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Kullanıcı banlı mı (active=false) kontrol et
            if (!user.getActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your account has been banned. Please contact support.");
            }
            
            // Stripe customer ID'si yoksa oluştur
            if (user.getStripeCustomerId() == null || user.getStripeCustomerId().isEmpty()) {
                try {
                    Customer customer = stripeService.createCustomer(user.getEmail());
                    user.setStripeCustomerId(customer.getId());
                    userService.save(user); // Stripe ID'yi DB'ye kaydet
                } catch (StripeException e) {
                    return ResponseEntity.badRequest().body("Stripe customer creation failed: " + e.getMessage());
                }
            }

            // Token oluştur ve dön (active durumunu da ekle)
            String token = jwtUtil.generateToken(user.getEmail(), user.getActive());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
        }
    }
}
