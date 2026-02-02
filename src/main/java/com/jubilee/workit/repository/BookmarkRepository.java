package com.jubilee.workit.repository;

import com.jubilee.workit.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);
    Optional<Bookmark> findByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);
    Page<Bookmark> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
