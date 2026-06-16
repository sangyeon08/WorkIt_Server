package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ChatMessageDto;
import com.jubilee.workit.dto.ChatMessageRequest;
import com.jubilee.workit.dto.ChatRoomDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.entity.ChatMessage;
import com.jubilee.workit.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * WebSocket 메시지 전송.
     * STOMP CONNECT 시 WebSocketAuthInterceptor가 JWT를 검증하고 Principal을 세팅함.
     * 클라이언트는 CONNECT 프레임 헤더에 Authorization: Bearer <token>을 포함해야 함.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        if (principal == null) {
            return; // 미인증 연결 무시
        }

        Long senderId = (Long) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        ChatMessage savedMessage = chatService.saveMessage(
                request.getChatRoomId(),
                senderId,
                request.getMessage()
        );

        ChatMessageDto messageDto = new ChatMessageDto();
        messageDto.setId(savedMessage.getId());
        messageDto.setChatRoomId(savedMessage.getChatRoom().getId());
        messageDto.setSenderId(savedMessage.getSender().getId());
        messageDto.setSenderName(savedMessage.getSender().getEmail());
        messageDto.setMessage(savedMessage.getMessage());
        messageDto.setRead(savedMessage.isRead());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());

        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getChatRoomId(),
                messageDto
        );
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleWebSocketException(Exception e) {
        return e.getMessage();
    }

    // ── REST API ─────────────────────────────────────────────────────────────

    @GetMapping("/api/chat/rooms")
    @Operation(summary = "채팅방 목록 조회", description = "사용자가 참여한 채팅방 목록을 조회합니다.")
    @ResponseBody
    public ResponseEntity<PageResponse<ChatRoomDto>> getChatRooms(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.getRooms(userId, page, size));
    }

    @PostMapping("/api/chat/rooms")
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성하거나 기존 채팅방을 반환합니다.")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> createOrGetChatRoom(
            @RequestParam Long jobPostingId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.createOrGetRoom(jobPostingId, userId));
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다. (오래된 순)")
    @ResponseBody
    public ResponseEntity<PageResponse<ChatMessageDto>> getMessages(
            @PathVariable Long roomId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(chatService.getMessages(roomId, userId, page, size));
    }

    @PutMapping("/api/chat/rooms/{roomId}/read")
    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 메시지를 읽음 처리합니다.")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long roomId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/unread-count")
    @Operation(summary = "읽지 않은 메시지 개수", description = "사용자의 전체 읽지 않은 메시지 개수를 조회합니다.")
    @ResponseBody
    public ResponseEntity<UnreadCountDto> getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new UnreadCountDto(chatService.getTotalUnreadCount(userId)));
    }
}
