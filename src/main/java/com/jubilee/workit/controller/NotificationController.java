package com.jubilee.workit.controller;

import com.jubilee.workit.dto.NotificationDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "알림 목록 조회", description = "로그인한 사용자의 알림 목록을 조회합니다.")
    public PageResponse<NotificationDto> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.listByUser(userId, page, size);
    }

    @GetMapping(value = "/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "읽지 않은 알림 수 조회", description = "읽지 않은 알림의 총 개수를 반환합니다.")
    public UnreadCountDto getUnreadCount(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.getUnreadCount(userId);
    }

    @PatchMapping(value = "/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponse(responseCode = "204", description = "처리 성공")
    public void markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long id,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.markAsRead(id, userId);
    }

    @PatchMapping(value = "/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "전체 알림 읽음 처리", description = "모든 알림을 읽음 상태로 변경합니다.")
    @ApiResponse(responseCode = "204", description = "처리 성공")
    public void markAllAsRead(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.markAllAsRead(userId);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public void deleteNotification(
            @Parameter(description = "알림 ID") @PathVariable Long id,
            Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        notificationService.deleteNotification(id, userId);
    }
}
