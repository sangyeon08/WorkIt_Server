package com.jubilee.workit.repository;

import com.jubilee.workit.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지 목록 조회
    Page<ChatMessage> findByChatRoom_Id(Long chatRoomId, Pageable pageable);

    // 특정 채팅방에서 특정 사용자가 보내지 않은 메시지 목록
    List<ChatMessage> findByChatRoom_IdAndSender_IdNot(Long chatRoomId, Long senderId);

    // 특정 채팅방에서 읽지 않은 메시지 개수 (상대방이 보낸 메시지)
    Long countByChatRoom_IdAndSender_IdNotAndReadFalse(Long chatRoomId, Long currentUserId);

    // 특정 사용자가 받은 읽지 않은 전체 메시지 개수
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id IN " +
            "(SELECT cr.id FROM ChatRoom cr WHERE cr.employer.id = :userId OR cr.applicant.id = :userId) " +
            "AND m.sender.id != :userId AND m.read = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);

    // 특정 채팅방의 읽지 않은 메시지를 읽음 처리
    @Modifying
    @Query("UPDATE ChatMessage m SET m.read = true WHERE m.chatRoom.id = :chatRoomId AND m.sender.id != :userId AND m.read = false")
    void markMessagesAsReadInRoom(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}

