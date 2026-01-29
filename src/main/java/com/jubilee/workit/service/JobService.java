package com.jubilee.workit.service;

import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.entity.JobPosting;
import com.jubilee.workit.repository.JobPostingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobPostingRepository jobPostingRepository;

    public JobService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
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
