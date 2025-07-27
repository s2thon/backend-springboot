package com.example.backend.service;

import com.example.backend.dto.ProductDto;
import java.util.List;

public interface WishlistService {
    /** Oturumlu kullanıcıya ait favori ürünleri döner */
    List<ProductDto> getWishlist(Long userId);

    /** Favori listesine ürün ekler */
    void addToWishlist(Long userId, Long productId);

    /** Favori listesinden ürün çıkarır */
    void removeFromWishlist(Long userId, Long productId);
}
