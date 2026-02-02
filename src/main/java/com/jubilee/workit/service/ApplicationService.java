package com.jubilee.workit.service;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.Application;
import com.jubilee.workit.entity.JobPosting;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.repository.ApplicationRepository;
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
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobPostingRepository jobPostingRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApplicationDto apply(Long jobId, Long userId, ApplyRequest request) {
        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 공고 확인
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        // 중복 지원 확인
        if (applicationRepository.existsByUser_IdAndJobPosting_Id(userId, jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 지원한 공고입니다.");
        }

        // 지원 생성
        Application application = new Application();
        application.setUser(user);
        application.setJobPosting(job);
        application.setCoverLetter(request.getCoverLetter());
        application.setPhone(request.getPhone());
        application.setEmail(request.getEmail());
        application.setStatus("PENDING");

        Application saved = applicationRepository.save(application);
        return toDto(saved);
    }

    public PageResponse<ApplicationDto> getMyApplications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> result = applicationRepository.findByUser_IdOrderByAppliedAtDesc(userId, pageable);
        return PageResponse.of(result.map(this::toDto));
    }

    public ApplicationDetailDto getApplicationDetail(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없습니다."));

        // 본인의 지원 내역인지 확인
        if (!application.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        return toDetailDto(application);
    }

    @Transactional
    public void cancelApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없습니다."));

        // 본인의 지원 내역인지 확인
        if (!application.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        // 이미 처리된 지원은 취소 불가
        if ("ACCEPTED".equals(application.getStatus()) || "REJECTED".equals(application.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "이미 처리된 지원은 취소할 수 없습니다.");
        }

        application.setStatus("WITHDRAWN");
        applicationRepository.save(application);
    }

    private ApplicationDto toDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJobPosting().getId());
        dto.setJobTitle(app.getJobPosting().getTitle());
        dto.setCompanyName(app.getJobPosting().getCompany() != null ?
                app.getJobPosting().getCompany().getName() : null);
        dto.setCompanyLogoUrl(app.getJobPosting().getCompany() != null ?
                app.getJobPosting().getCompany().getLogoUrl() : null);
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
        return dto;
    }

    private ApplicationDetailDto toDetailDto(Application app) {
        ApplicationDetailDto dto = new ApplicationDetailDto();
        dto.setId(app.getId());
        dto.setJob(toJobCardDto(app.getJobPosting()));
        dto.setCoverLetter(app.getCoverLetter());
        dto.setPhone(app.getPhone());
        dto.setEmail(app.getEmail());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setUpdatedAt(app.getUpdatedAt());
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