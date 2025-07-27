package com.example.backend.dto;

import java.time.LocalDateTime;

public record ReviewDto(
    Long id,
    int rating,
    String comment,
    Long productId,
    Long userId,
    LocalDateTime createdAt
) {}
