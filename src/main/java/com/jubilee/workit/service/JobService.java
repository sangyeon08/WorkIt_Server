package com.jubilee.workit.service;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.*;
import com.jubilee.workit.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobPostingRepository jobPostingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(JobPostingRepository jobPostingRepository,
                      BookmarkRepository bookmarkRepository,
                      ApplicationRepository applicationRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.applicationRepository = applicationRepository;
    }

    // 근처 공고 검색
    public PageResponse<JobCardDto> getNearby(double latitude, double longitude, double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findNearby(latitude, longitude, radiusKm, pageable);
        return PageResponse.of(result.map(this::toCard));
    }

    // 공고 상세 조회
    public JobDetailDto getJobDetail(Long jobId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        JobDetailDto dto = new JobDetailDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());

        // 기업
        if (job.getCompany() != null) {
            CompanyDto companyDto = new CompanyDto();
            companyDto.setId(job.getCompany().getId());
            companyDto.setName(job.getCompany().getName());
            companyDto.setDescription(job.getCompany().getDescription());
            companyDto.setLogoUrl(job.getCompany().getLogoUrl());
            companyDto.setWebsiteUrl(job.getCompany().getWebsiteUrl());
            dto.setCompany(companyDto);
        }

        // 위치
        if (job.getLocation() != null) {
            LocationDto locationDto = new LocationDto();
            locationDto.setId(job.getLocation().getId());
            locationDto.setName(job.getLocation().getName());
            locationDto.setCity(job.getLocation().getCity());
            locationDto.setCountry(job.getLocation().getCountry());
            locationDto.setLatitude(job.getLocation().getLatitude());
            locationDto.setLongitude(job.getLocation().getLongitude());
            dto.setLocation(locationDto);
        }

        dto.setCompensationAmount(job.getCompensationAmount());
        dto.setCompensationType(job.getCompensationType());
        dto.setJobType(job.getJobType());
        dto.setDurationType(job.getDurationType());

        List<String> categoryNames = job.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        dto.setCategoryNames(categoryNames);

        dto.setImageUrl(job.getImageUrl());
        dto.setHot(job.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(job.getPublishedAt() != null && job.getPublishedAt().isAfter(dayAgo));

        if (userId != null) {
            boolean isBookmarked = bookmarkRepository.existsByUser_IdAndJobPosting_Id(userId, jobId);
            dto.setBookmarked(isBookmarked);
        } else {
            dto.setBookmarked(false);
        }

        dto.setPublishedAt(job.getPublishedAt());
        dto.setExpiresAt(job.getExpiresAt());

        Integer applicantCount = applicationRepository.countByJobPostingId(jobId);
        dto.setApplicantCount(applicantCount != null ? applicantCount : 0);

        return dto;
    }

    public PageResponse<JobCardDto> getHotPostings(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findHotPostings(p);
        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> getLatestPostings(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findLatestPostings(p);
        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> getPostings(String filter, Long locationId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result;

        if ("by_location".equalsIgnoreCase(filter) && locationId != null) {
            result = jobPostingRepository.findByLocationId(locationId, p);
        } else if ("long_term".equalsIgnoreCase(filter)) {
            result = jobPostingRepository.findLongTermPostings(p);
        } else if ("short_term".equalsIgnoreCase(filter)) {
            result = jobPostingRepository.findShortTermPostings(p);
        } else {
            result = jobPostingRepository.findLatestPostings(p);
        }

        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> search(String q, int page, int size) {
        if (q == null || q.isBlank()) {
            return getLatestPostings(page, size);
        }
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.search(q.trim(), p);
        return PageResponse.of(result.map(this::toCard));
    }

    private JobCardDto toCard(JobPosting j) {
        JobCardDto dto = new JobCardDto();
        dto.setId(j.getId());
        dto.setTitle(j.getTitle());
        dto.setCompanyName(j.getCompany() != null ? j.getCompany().getName() : null);
        dto.setCompanyLogoUrl(j.getCompany() != null ? j.getCompany().getLogoUrl() : null);
        dto.setLocationName(j.getLocation() != null ? j.getLocation().getName() : null);
        dto.setCompensationAmount(j.getCompensationAmount());
        dto.setCompensationType(j.getCompensationType());
        List<String> cats = j.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        dto.setCategoryNames(cats);
        dto.setImageUrl(j.getImageUrl());
        dto.setHot(j.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(j.getPublishedAt() != null && j.getPublishedAt().isAfter(dayAgo));
        dto.setPublishedAt(j.getPublishedAt());
        return dto;
    }
}