package com.jubilee.workit.service;

import com.jubilee.workit.dto.BookmarkDto;
import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.entity.Bookmark;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.entity.JobPosting;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.BookmarkRepository;
import com.jubilee.workit.repository.JobPostingRepository;
import com.jubilee.workit.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                           JobPostingRepository jobPostingRepository,
                           UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addBookmark(Long jobId, Long userId) {
        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 공고 확인
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        // 이미 북마크한 경우 중복 방지
        if (bookmarkRepository.existsByUser_IdAndJobPosting_Id(userId, jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 찜한 공고입니다.");
        }

        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setJobPosting(job);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long jobId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findByUser_IdAndJobPosting_Id(userId, jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "찜한 공고가 아닙니다."));

        bookmarkRepository.delete(bookmark);
    }

    public PageResponse<BookmarkDto> getMyBookmarks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Bookmark> result = bookmarkRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.of(result.map(this::toDto));
    }

    public boolean isBookmarked(Long jobId, Long userId) {
        return bookmarkRepository.existsByUser_IdAndJobPosting_Id(userId, jobId);
    }

    private BookmarkDto toDto(Bookmark bookmark) {
        BookmarkDto dto = new BookmarkDto();
        dto.setId(bookmark.getId());
        dto.setJob(toJobCardDto(bookmark.getJobPosting()));
        dto.setCreatedAt(bookmark.getCreatedAt());
        return dto;
    }

    private JobCardDto toJobCardDto(JobPosting j) {
        JobCardDto dto = new JobCardDto();
        dto.setId(j.getId());
        dto.setTitle(j.getTitle());
        dto.setCompanyName(j.getCompany() != null ? j.getCompany().getName() : null);
        dto.setCompanyLogoUrl(j.getCompany() != null ? j.getCompany().getLogoUrl() : null);
        dto.setLocationName(j.getLocation() != null ? j.getLocation().getName() : null);
        dto.setCompensationAmount(j.getCompensationAmount());
        dto.setCompensationType(j.getCompensationType());
        dto.setCategoryNames(j.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));
        dto.setImageUrl(j.getImageUrl());
        dto.setHot(j.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(j.getPublishedAt() != null && j.getPublishedAt().isAfter(dayAgo));
        dto.setPublishedAt(j.getPublishedAt());
        return dto;
    }
}