package com.jubilee.workit.service;

import com.jubilee.workit.dto.ChatMessageDto;
import com.jubilee.workit.dto.ChatRoomDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.entity.*;
import com.jubilee.workit.repository.*;
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
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       ChatMessageRepository chatMessageRepository,
                       JobPostingRepository jobPostingRepository,
                       UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    // 채팅 방 목록 조회
    public PageResponse<ChatRoomDto> getRooms(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ChatRoom> result = chatRoomRepository.findByEmployer_IdOrApplicant_Id(userId, userId, pageable);
        return PageResponse.of(result.map(r -> toChatRoomDto(r, userId)));
    }

    // 채팅 방 생성 또는 기존 방 반환
    @Transactional
    public ChatRoomDto createOrGetRoom(Long jobPostingId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 이미 존재하는 방이 있는지 확인
        ChatRoom existing = chatRoomRepository.findByJobPosting_IdAndApplicant_Id(jobPostingId, userId)
                .orElse(null);

        if (existing != null) {
            return toChatRoomDto(existing, userId);
        }

        // 새 채팅 방 생성
        ChatRoom newRoom = new ChatRoom();
        newRoom.setJobPosting(job);
        newRoom.setApplicant(user);
        newRoom.setEmployer(job.getEmployer());

        ChatRoom saved = chatRoomRepository.save(newRoom);
        return toChatRoomDto(saved, userId);
    }

    // 메시지 목록 조회
    public PageResponse<ChatMessageDto> getMessages(Long roomId, Long userId, int page, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        // 권한 확인: 본인의 채팅 방인지
        if (!room.getEmployer().getId().equals(userId) && !room.getApplicant().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ChatMessage> result = chatMessageRepository.findByChatRoom_Id(roomId, pageable);
        return PageResponse.of(result.map(this::toChatMessageDto));
    }

    // 읽음 표시
    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        // 권한 확인
        if (!room.getEmployer().getId().equals(userId) && !room.getApplicant().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        // 상대방이 보낸 메시지만 읽음 처리
        chatMessageRepository.findByChatRoom_IdAndSender_IdNot(roomId, userId)
                .forEach(msg -> {
                    if (!msg.isRead()) {
                        msg.setRead(true);
                    }
                });
    }

    // 메시지 저장 (WebSocket에서 호출)
    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(room);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setRead(false);

        return chatMessageRepository.save(chatMessage);
    }

    // 전체 읽지 않은 메시지 개수 조회
    public Long getTotalUnreadCount(Long userId) {
        return chatMessageRepository.countUnreadMessagesForUser(userId);
    }

    // DTO 변환
    private ChatRoomDto toChatRoomDto(ChatRoom room, Long currentUserId) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(room.getId());
        dto.setJobPostingId(room.getJobPosting().getId());
        dto.setJobTitle(room.getJobPosting().getTitle());

        // 상대방 정보 (employer 또는 applicant)
        if (room.getEmployer() != null && room.getEmployer().getId().equals(currentUserId)) {
            dto.setOtherUserId(room.getApplicant().getId());
            dto.setOtherUserName(room.getApplicant().getEmail());
        } else if (room.getApplicant() != null) {
            dto.setOtherUserId(room.getEmployer() != null ? room.getEmployer().getId() : null);
            dto.setOtherUserName(room.getEmployer() != null ? room.getEmployer().getEmail() : "Unknown");
        }

        dto.setCreatedAt(room.getCreatedAt());

        // 미읽음 메시지 수
        Long unreadCount = chatMessageRepository.countByChatRoom_IdAndSender_IdNotAndReadFalse(room.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);

        return dto;
    }

    private ChatMessageDto toChatMessageDto(ChatMessage msg) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(msg.getId());
        dto.setChatRoomId(msg.getChatRoom().getId());
        dto.setSenderId(msg.getSender().getId());
        dto.setSenderName(msg.getSender().getEmail());
        dto.setMessage(msg.getMessage());
        dto.setRead(msg.isRead());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}