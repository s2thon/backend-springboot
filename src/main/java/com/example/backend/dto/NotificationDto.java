// src/main/java/com/example/backend/dto/NotificationDto.java
package com.example.backend.dto;

import java.time.LocalDateTime;

public record NotificationDto(
    Long id,
    String message,
    String type,
    boolean read,
    Long userId,
    LocalDateTime createdAt
) {}
