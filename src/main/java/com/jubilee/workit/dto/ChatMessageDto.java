package com.jubilee.workit.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {

    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    // 기본 생성자
    public ChatMessageDto() {}

    // 전체 생성자
    public ChatMessageDto(Long id, Long chatRoomId, Long senderId, String senderName,
                          String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

