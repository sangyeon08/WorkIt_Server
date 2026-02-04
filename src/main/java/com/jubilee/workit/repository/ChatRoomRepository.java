package com.jubilee.workit.repository;

import com.jubilee.workit.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 특정 사용자가 참여한 채팅방 목록 조회 (employer 또는 applicant)
    Page<ChatRoom> findByEmployer_IdOrApplicant_Id(Long employerId, Long applicantId, Pageable pageable);

    // 특정 공고와 지원자로 채팅방 찾기
    Optional<ChatRoom> findByJobPosting_IdAndApplicant_Id(Long jobPostingId, Long applicantId);

    // 채팅방 존재 여부 확인
    boolean existsByJobPosting_IdAndApplicant_Id(Long jobPostingId, Long applicantId);
}

