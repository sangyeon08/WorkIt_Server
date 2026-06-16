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

    public PageResponse<ChatRoomDto> getRooms(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ChatRoom> result = chatRoomRepository.findByEmployer_IdOrApplicant_Id(userId, userId, pageable);
        return PageResponse.of(result.map(r -> toChatRoomDto(r, userId)));
    }

    @Transactional
    public ChatRoomDto createOrGetRoom(Long jobPostingId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        if (job.getEmployer() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "해당 공고에 담당자 정보가 없습니다. 구인자가 직접 등록한 공고에서만 채팅이 가능합니다.");
        }

        if (job.getEmployer().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "자신이 등록한 공고에는 채팅방을 생성할 수 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ChatRoom existing = chatRoomRepository.findByJobPosting_IdAndApplicant_Id(jobPostingId, userId)
                .orElse(null);

        if (existing != null) {
            return toChatRoomDto(existing, userId);
        }

        ChatRoom newRoom = new ChatRoom();
        newRoom.setJobPosting(job);
        newRoom.setApplicant(user);
        newRoom.setEmployer(job.getEmployer());

        return toChatRoomDto(chatRoomRepository.save(newRoom), userId);
    }

    public PageResponse<ChatMessageDto> getMessages(Long roomId, Long userId, int page, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        if (!isParticipant(room, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ChatMessage> result = chatMessageRepository.findByChatRoom_Id(roomId, pageable);
        return PageResponse.of(result.map(this::toChatMessageDto));
    }

    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        if (!isParticipant(room, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        chatMessageRepository.markMessagesAsReadInRoom(roomId, userId);
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅 방을 찾을 수 없습니다."));

        if (!isParticipant(room, senderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(room);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setRead(false);

        return chatMessageRepository.save(chatMessage);
    }

    public Long getTotalUnreadCount(Long userId) {
        return chatMessageRepository.countUnreadMessagesForUser(userId);
    }

    private boolean isParticipant(ChatRoom room, Long userId) {
        boolean isEmployer = room.getEmployer() != null && room.getEmployer().getId().equals(userId);
        boolean isApplicant = room.getApplicant() != null && room.getApplicant().getId().equals(userId);
        return isEmployer || isApplicant;
    }

    private ChatRoomDto toChatRoomDto(ChatRoom room, Long currentUserId) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(room.getId());
        dto.setJobPostingId(room.getJobPosting().getId());
        dto.setJobTitle(room.getJobPosting().getTitle());

        boolean currentUserIsEmployer = room.getEmployer() != null
                && room.getEmployer().getId().equals(currentUserId);

        if (currentUserIsEmployer) {
            dto.setOtherUserId(room.getApplicant().getId());
            dto.setOtherUserName(room.getApplicant().getEmail());
        } else {
            dto.setOtherUserId(room.getEmployer() != null ? room.getEmployer().getId() : null);
            dto.setOtherUserName(room.getEmployer() != null ? room.getEmployer().getEmail() : "Unknown");
        }

        dto.setCreatedAt(room.getCreatedAt());

        Long unreadCount = chatMessageRepository.countByChatRoom_IdAndSender_IdNotAndReadFalse(
                room.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);

        // 마지막 메시지 미리보기
        chatMessageRepository.findTopByChatRoom_IdOrderByCreatedAtDesc(room.getId())
                .ifPresent(last -> {
                    dto.setLastMessage(last.getMessage());
                    dto.setLastMessageTime(last.getCreatedAt());
                });

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
