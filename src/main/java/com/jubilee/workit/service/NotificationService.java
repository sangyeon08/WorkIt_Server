package com.jubilee.workit.service;

import com.jubilee.workit.dto.NotificationDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.entity.Notification;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.NotificationRepository;
import com.jubilee.workit.repository.UserRepository;
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
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
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
        notificationRepository.markAllAsReadByUserId(userId);
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
