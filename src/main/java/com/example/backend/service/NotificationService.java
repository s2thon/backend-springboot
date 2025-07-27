// src/main/java/com/example/backend/service/NotificationService.java
package com.example.backend.service;

import com.example.backend.dto.NotificationDto;
import com.example.backend.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification create(NotificationDto dto);
    List<NotificationDto> findByUserId(Long userId);
    void markAsRead(Long id);
}
