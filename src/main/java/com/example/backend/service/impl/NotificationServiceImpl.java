package com.example.backend.service.impl;

import com.example.backend.dto.NotificationDto;
import com.example.backend.entity.Notification;
import com.example.backend.entity.User;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepo;

    @Override
    public Notification create(NotificationDto dto) {
        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Notification notification = Notification.builder()
                .message(dto.message())
                .type(dto.type())
                .read(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return repo.save(notification);
    }

    @Override
    public List<NotificationDto> findByUserId(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> new NotificationDto(
                        n.getId(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getUser().getId(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification"));
        notification.setRead(true);
        repo.save(notification);
    }
}
