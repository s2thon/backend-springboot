// src/main/java/com/example/backend/dto/ProductDto.java
package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
    Long id,
    String name,
    String description,
    String image,
    BigDecimal price,
    Integer stock,
    BigDecimal rate,
    Integer reviewCount,
    Long categoryId,
    List<ReviewDto> reviews ,  // Yeni alan: yorumların listesi
    Long sellerId
) {}
