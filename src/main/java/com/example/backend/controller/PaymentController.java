package com.example.backend.controller;

import com.example.backend.dto.PaymentDto;
import com.example.backend.entity.Payment;
import com.example.backend.entity.User;
import com.example.backend.service.PaymentService;
import com.example.backend.service.StripeService;
import com.example.backend.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PaymentMethodListParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final StripeService stripeService;

    /** 1) Customer yaratır */
    @PostMapping("/customers")
    public ResponseEntity<Map<String, String>> createCustomer(@RequestBody Map<String, String> body)
            throws StripeException {
        String email = body.get("email");
        if (email == null)
            return ResponseEntity.badRequest().build();
        Customer customer = stripeService.createCustomer(email);
        return ResponseEntity.ok(Map.of("id", customer.getId()));
    }

    /** 2) Basit PaymentIntent oluşturur (clientSecret dön) */
    @PostMapping("/payment-intents")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> body)
            throws StripeException {
        
        try {
            Object amt = body.get("amount");
            Object cur = body.get("currency");
            Object customerId = body.get("customerId");

            if (amt == null || cur == null || customerId == null) {
                System.out.println("Eksik parametreler: " + body);
                return ResponseEntity.badRequest().body(Map.of("error", "Missing parameters"));
            }

            long amount = Long.parseLong(amt.toString());
            String currency = cur.toString();
            String customer = customerId.toString();

            System.out.println("Payment intent creating for: " + amount + " " + currency + " " + customer);
            
            PaymentIntent intent = stripeService.createPaymentIntent(amount, currency, customer);
            System.out.println("Payment intent created: " + intent.getId());
            
            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
        } catch (Exception e) {
            System.err.println("Payment intent error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /** 3) SetupIntent oluşturup clientSecret döner (kart kaydetme için) */
    @PostMapping("/setup-intent")
    public ResponseEntity<Map<String, String>> createSetupIntent(@RequestBody Map<String, String> body)
            throws StripeException {
        String customerId = body.get("customerId");
        if (customerId == null)
            return ResponseEntity.badRequest().build();
        SetupIntent intent = stripeService.createSetupIntent(customerId);
        return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));
    }

    /** 4) Bir customer’ın kayıtlı PaymentMethodlarını listeler */
    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethod>> listPaymentMethods(@RequestParam String customerId)
            throws StripeException {
        // Tip:CARD olarak sınırlıyoruz
        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();
        List<PaymentMethod> methods = stripeService.listPaymentMethods(params);
        return ResponseEntity.ok(methods);
    }

    /** 5) Stripe → DB kaydı ile tam halkayı kurar */
    @PostMapping("/stripe-intent")
    public ResponseEntity<PaymentDto> createIntent(@RequestBody PaymentDto dto) throws StripeException {
        User u = userService.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        Customer c = stripeService.createCustomer(u.getEmail());
        u.setStripeCustomerId(c.getId());
        userService.save(u);

        long cents = dto.amount().multiply(BigDecimal.valueOf(100)).longValue();

        // ← Üçüncü parametre olarak customer ID eklenmeli
        PaymentIntent intent = stripeService.createPaymentIntent(cents, dto.currency(), c.getId());

        // Geri kalanlar aynı
        Payment p = toEntity(dto);
        p.setUser(u);
        p.setStripeCustomerId(c.getId());
        p.setStripePaymentIntentId(intent.getId());
        p.setPaymentMethod(intent.getPaymentMethod());
        p.setStatus(intent.getStatus());

        if (intent.getPaymentMethod() != null) {
            PaymentMethod pm = stripeService.retrievePaymentMethod(intent.getPaymentMethod());
            PaymentMethod.Card card = pm.getCard();
            if (card != null) {
                p.setCardBrand(card.getBrand());
                p.setCardLastFour(card.getLast4());
                p.setCardExpirationMonth(card.getExpMonth().intValue());
                p.setCardExpirationYear(card.getExpYear().intValue());
            }
        }

        Payment saved = paymentService.save(p);
        return ResponseEntity.ok(toDto(saved));
    }

    /** 6) CRUD: ekstra “kullanıcıya göre” listeleme */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> listByUser(@PathVariable Long userId) {
        List<PaymentDto> dtos = paymentService.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // —————— ↓ Mevcut generic CRUD endpoint’leriniz ↓ ——————

    @GetMapping
    public List<PaymentDto> listAll() {
        return paymentService.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return paymentService.findById(id)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto dto) {
        Payment p = toEntity(dto);
        User u = userService.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
        p.setUser(u);
        p.setStripeCustomerId(dto.stripeCustomerId());
        p.setStripePaymentIntentId(dto.stripePaymentIntentId());
        Payment saved = paymentService.save(p);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable Long id, @RequestBody PaymentDto dto) {
        return paymentService.findById(id)
                .map(existing -> {
                    Payment toSave = toEntity(dto);
                    toSave.setId(id);
                    User u = userService.findById(dto.userId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));
                    toSave.setUser(u);
                    toSave.setStripeCustomerId(dto.stripeCustomerId());
                    toSave.setStripePaymentIntentId(dto.stripePaymentIntentId());
                    Payment updated = paymentService.save(toSave);
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (paymentService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        paymentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // —————— DTO ↔ Entity dönüştürücü metodlar ——————

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getId(),
                p.getPaymentMethod(),
                p.getStripeCustomerId(),
                p.getStripePaymentIntentId(),
                p.getCardLastFour(),
                p.getCardBrand(),
                p.getCardExpirationMonth(),
                p.getCardExpirationYear(),
                p.getAmount(),
                p.getCurrency(),
                p.getStatus(),
                p.getUser().getId());
    }

    private Payment toEntity(PaymentDto d) {
        Payment p = new Payment();
        p.setPaymentMethod(d.paymentMethod());
        p.setCardLastFour(d.cardLastFour());
        p.setCardBrand(d.cardBrand());
        p.setCardExpirationMonth(d.cardExpirationMonth());
        p.setCardExpirationYear(d.cardExpirationYear());
        p.setAmount(d.amount());
        p.setCurrency(d.currency());
        p.setStatus(d.status());
        return p;
    }
}
