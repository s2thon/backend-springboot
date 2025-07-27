package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ProductDto;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.WishlistService;

@RestController
@RequestMapping("/api/lists")
public class WishlistController {
  @Autowired private WishlistService svc;
  @Autowired private JwtUtil jwtUtil;

  // 1) Listeyi getir
  @GetMapping
  public List<ProductDto> list(@RequestHeader("Authorization") String token) {
    Long userId = jwtUtil.extractUserId(token);
    return svc.getWishlist(userId);
  }

  // 2) Ekle
  @PostMapping("/{productId}")
  public void add(@PathVariable Long productId,
                  @RequestHeader("Authorization") String token) {
    Long userId = jwtUtil.extractUserId(token);
    svc.addToWishlist(userId, productId);
  }

  // 3) Kaldır
  @DeleteMapping("/{productId}")
  public void remove(@PathVariable Long productId,
                     @RequestHeader("Authorization") String token) {
    Long userId = jwtUtil.extractUserId(token);
    svc.removeFromWishlist(userId, productId);
  }
}
