package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ChatMessageDto;
import com.jubilee.workit.dto.ChatMessageRequest;
import com.jubilee.workit.dto.ChatRoomDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.UnreadCountDto;
import com.jubilee.workit.entity.ChatMessage;
import com.jubilee.workit.service.ChatService;
import com.jubilee.workit.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Tag(name = "Chat", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    public ChatController(ChatService chatService,
                          SimpMessagingTemplate messagingTemplate,
                          JwtUtil jwtUtil) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtil = jwtUtil;
    }

    // WebSocket을 통한 메시지 전송
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request,
                            @Header("Authorization") String token) {
        // JWT에서 사용자 ID 추출
        String jwt = token.replace("Bearer ", "");
        Long senderId = jwtUtil.getUserIdFromToken(jwt);
        String email = jwtUtil.parseToken(jwt).get("email", String.class);

        // 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(
                request.getChatRoomId(),
                senderId,
                request.getMessage()
        );

        // DTO 변환
        ChatMessageDto messageDto = new ChatMessageDto();
        messageDto.setId(savedMessage.getId());
        messageDto.setChatRoomId(savedMessage.getChatRoom().getId());
        messageDto.setSenderId(savedMessage.getSender().getId());
        messageDto.setSenderName(savedMessage.getSender().getEmail());
        messageDto.setMessage(savedMessage.getMessage());
        messageDto.setRead(savedMessage.isRead());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());

        // 채팅방의 모든 참여자에게 메시지 전송
        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getChatRoomId(),
                messageDto
        );
    }

    // REST API: 채팅방 목록 조회
    @GetMapping("/api/chat/rooms")
    @Operation(summary = "채팅방 목록 조회", description = "사용자가 참여한 채팅방 목록을 조회합니다.")
    @ResponseBody
    public ResponseEntity<PageResponse<ChatRoomDto>> getChatRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());
        PageResponse<ChatRoomDto> rooms = chatService.getRooms(userId, page, size);
        return ResponseEntity.ok(rooms);
    }

    // REST API: 채팅방 생성 또는 가져오기
    @PostMapping("/api/chat/rooms")
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성하거나 기존 채팅방을 반환합니다.")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> createOrGetChatRoom(
            @RequestParam Long jobPostingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        ChatRoomDto room = chatService.createOrGetRoom(jobPostingId, userId);
        return ResponseEntity.ok(room);
    }

    // REST API: 메시지 목록 조회
    @GetMapping("/api/chat/rooms/{roomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @ResponseBody
    public ResponseEntity<PageResponse<ChatMessageDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());
        PageResponse<ChatMessageDto> messages = chatService.getMessages(roomId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

    // REST API: 메시지 읽음 처리
    @PutMapping("/api/chat/rooms/{roomId}/read")
    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 메시지를 읽음 처리합니다.")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }

    // REST API: 읽지 않은 메시지 개수 조회
    @GetMapping("/api/chat/unread-count")
    @Operation(summary = "읽지 않은 메시지 개수", description = "사용자의 전체 읽지 않은 메시지 개수를 조회합니다.")
    @ResponseBody
    public ResponseEntity<UnreadCountDto> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Long count = chatService.getTotalUnreadCount(userId);
        return ResponseEntity.ok(new UnreadCountDto(count));
    }
}
