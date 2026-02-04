package com.jubilee.workit.dto;

public class ChatMessageRequest {

    private Long chatRoomId;
    private String message;

    // 기본 생성자
    public ChatMessageRequest() {}

    // 생성자
    public ChatMessageRequest(Long chatRoomId, String message) {
        this.chatRoomId = chatRoomId;
        this.message = message;
    }

    // Getters and Setters
    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}