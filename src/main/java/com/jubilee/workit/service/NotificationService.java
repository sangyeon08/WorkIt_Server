package com.jubilee.workit.service;

import com.jubilee.workit.dto.NotificationDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.entity.Notification;
import com.jubilee.workit.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public PageResponse<NotificationDto> listByUser(Long userId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> result = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, p);
        return PageResponse.of(result.map(this::toDto));
    }

    public UnreadCountDto getUnreadCount(Long userId) {
        Long count = notificationRepository.countByUser_IdAndReadFalse(userId);
        return new UnreadCountDto(count);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

        notifications.getContent().forEach(notification -> {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        });

        notificationRepository.saveAll(notifications.getContent());
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
