package com.example.backend.service.impl;

import com.example.backend.dto.ProductDto;
import com.example.backend.entity.WishlistItem;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.WishlistItemRepository;
import com.example.backend.service.WishlistService;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final JwtUtil jwtUtil;

    @Override
    public List<ProductDto> getWishlist(Long userId) {
        return wishlistRepo.findByUser_Id(userId).stream()
                .map(WishlistItem::getProduct)
                .map(p -> new ProductDto(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getImage(),
                        p.getPrice(),
                        p.getStock(),
                        p.getRate(),
                        p.getReviewCount(),
                        p.getCategory() != null ? p.getCategory().getId() : null,
                        List.of(),
                        p.getSeller() != null ? p.getSeller().getId() : null // 🆕 sellerId eklendi
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void addToWishlist(Long userId, Long productId) {
        if (wishlistRepo.findByUser_IdAndProduct_Id(userId, productId).isEmpty()) {
            var item = new WishlistItem();
            item.setUser(userRepo.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId)));
            item.setProduct(productRepo.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId)));
            wishlistRepo.save(item);
        }
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepo.findByUser_IdAndProduct_Id(userId, productId)
                .ifPresent(wishlistRepo::delete);
    }
}
