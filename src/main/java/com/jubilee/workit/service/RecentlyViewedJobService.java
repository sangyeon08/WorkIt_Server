package com.jubilee.workit.service;

import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.RecentlyViewedJobDto;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.entity.JobPosting;
import com.jubilee.workit.entity.RecentlyViewedJob;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.JobPostingRepository;
import com.jubilee.workit.repository.RecentlyViewedJobRepository;
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
public class RecentlyViewedJobService {

    private final RecentlyViewedJobRepository recentlyViewedJobRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public RecentlyViewedJobService(RecentlyViewedJobRepository recentlyViewedJobRepository,
                                    JobPostingRepository jobPostingRepository,
                                    UserRepository userRepository) {
        this.recentlyViewedJobRepository = recentlyViewedJobRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void recordView(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        RecentlyViewedJob viewed = recentlyViewedJobRepository
                .findByUser_IdAndJobPosting_Id(userId, jobId)
                .orElseGet(RecentlyViewedJob::new);
        viewed.setUser(user);
        viewed.setJobPosting(job);
        viewed.setViewedAt(LocalDateTime.now());
        recentlyViewedJobRepository.save(viewed);
    }

    public PageResponse<RecentlyViewedJobDto> getMyRecentlyViewed(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("viewedAt").descending());
        Page<RecentlyViewedJob> result = recentlyViewedJobRepository.findByUser_IdOrderByViewedAtDesc(userId, pageable);
        return PageResponse.of(result.map(this::toDto));
    }

    @Transactional
    public void removeByJobId(Long userId, Long jobId) {
        recentlyViewedJobRepository.deleteByUser_IdAndJobPosting_Id(userId, jobId);
    }

    @Transactional
    public void removeManyByJobIds(Long userId, Iterable<Long> jobIds) {
        for (Long jobId : jobIds) {
            recentlyViewedJobRepository.deleteByUser_IdAndJobPosting_Id(userId, jobId);
        }
    }

    private RecentlyViewedJobDto toDto(RecentlyViewedJob viewed) {
        RecentlyViewedJobDto dto = new RecentlyViewedJobDto();
        dto.setId(viewed.getId());
        dto.setJob(toJobCardDto(viewed.getJobPosting()));
        dto.setViewedAt(viewed.getViewedAt());
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
        dto.setCategoryNames(j.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        dto.setImageUrl(j.getImageUrl());
        dto.setHot(j.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(j.getPublishedAt() != null && j.getPublishedAt().isAfter(dayAgo));
        dto.setPublishedAt(j.getPublishedAt());
        dto.setExpiresAt(j.getExpiresAt());
        return dto;
    }
}
