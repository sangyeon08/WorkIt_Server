package com.jubilee.workit.controller;

import com.jubilee.workit.dto.NotificationDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<NotificationDto> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.listByUser(userId, page, size);
    }

    @GetMapping(value = "/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public UnreadCountDto getUnreadCount(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.getUnreadCount(userId);
    }

    @PatchMapping(value = "/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(
            @PathVariable Long id,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.markAsRead(id, userId);
    }

    @PatchMapping(value = "/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.markAllAsRead(userId);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(
            @PathVariable Long id,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.deleteNotification(id, userId);
    }
}
