package com.example.backend.controller;

import com.example.backend.dto.NotificationDto;
import com.example.backend.entity.Notification;
import com.example.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // Yeni bildirim oluştur
    @PostMapping
    public ResponseEntity<NotificationDto> create(@RequestBody NotificationDto dto) {
        Notification created = service.create(dto);
        return ResponseEntity.ok(toDto(created));
    }

    // Kullanıcıya ait tüm bildirimleri getir
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    // Bildirimi okundu olarak işaretle
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Yardımcı toDto metodu
    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
            n.getId(),
            n.getMessage(),
            n.getType(),
            n.isRead(),
            n.getUser().getId(),
            n.getCreatedAt()
        );
    }
}
