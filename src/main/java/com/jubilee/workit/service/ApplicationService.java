package com.jubilee.workit.service;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.Application;
import com.jubilee.workit.entity.JobPosting;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.repository.ApplicationRepository;
import com.jubilee.workit.repository.JobPostingRepository;
import com.jubilee.workit.repository.ResumeRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final NotificationService notificationService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobPostingRepository jobPostingRepository,
                              UserRepository userRepository,
                              ResumeRepository resumeRepository,
                              NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.notificationService = notificationService;
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

        // 구인자는 지원 불가
        if ("EMPLOYER".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "구인자는 공고에 지원할 수 없습니다.");
        }

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

        // 구인자에게 새 지원 알림
        if (job.getEmployer() != null) {
            notificationService.createNotification(
                    job.getEmployer().getId(),
                    "'" + job.getTitle() + "' 공고에 새 지원자가 있습니다.",
                    "NEW_APPLICATION"
            );
        }

        return toDto(saved);
    }

    public PageResponse<ApplicationDto> getMyApplications(Long userId, String status, String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        String keyword = q == null || q.isBlank() ? null : q.trim();
        List<String> statuses = normalizeStatusToList(status);
        Page<Application> result = statuses != null
                ? applicationRepository.searchMyApplicationsByStatuses(userId, statuses, keyword, pageable)
                : applicationRepository.searchMyApplications(userId, null, keyword, pageable);
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
        String currentStatus = application.getStatus();
        if ("WITHDRAWN".equals(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 취소된 지원입니다.");
        }
        if ("ACCEPTED".equals(currentStatus) || "REJECTED".equals(currentStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "이미 처리된 지원은 취소할 수 없습니다.");
        }

        application.setStatus("WITHDRAWN");
        applicationRepository.save(application);
    }

    // ── 구인자용 ─────────────────────────────────────────────────────────────

    public PageResponse<EmployerApplicationDto> getJobApplications(
            Long jobId, Long employerId, String status, String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        String normalizedStatus = normalizeEmployerStatus(status);
        String keyword = q == null || q.isBlank() ? null : q.trim();
        Page<Application> result = applicationRepository.findByJobIdAndEmployerId(
                jobId, employerId, normalizedStatus, keyword, pageable);
        return PageResponse.of(result.map(this::toEmployerDto));
    }

    public EmployerApplicationDto getApplicationDetailForEmployer(Long applicationId, Long employerId) {
        Application application = applicationRepository.findByIdAndJobPosting_Employer_Id(applicationId, employerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없거나 접근 권한이 없습니다."));
        return toEmployerDto(application);
    }

    @Transactional
    public EmployerApplicationDto updateApplicationStatus(Long applicationId, Long employerId, String newStatus) {
        String normalized = switch (newStatus.trim().toUpperCase()) {
            case "REVIEWING" -> "REVIEWING";
            case "ACCEPTED", "PASSED" -> "ACCEPTED";
            case "REJECTED", "FAILED" -> "REJECTED";
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "올바른 상태값을 입력하세요. (REVIEWING, ACCEPTED, REJECTED)");
        };

        Application application = applicationRepository.findByIdAndJobPosting_Employer_Id(applicationId, employerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "지원 내역을 찾을 수 없거나 접근 권한이 없습니다."));

        if ("WITHDRAWN".equals(application.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "취소된 지원은 상태를 변경할 수 없습니다.");
        }

        application.setStatus(normalized);
        applicationRepository.save(application);

        // 구직자에게 상태 변경 알림
        String message = switch (normalized) {
            case "REVIEWING" -> "'" + application.getJobPosting().getTitle() + "' 지원이 검토 중입니다.";
            case "ACCEPTED" -> "'" + application.getJobPosting().getTitle() + "' 지원이 합격되었습니다!";
            case "REJECTED" -> "'" + application.getJobPosting().getTitle() + "' 지원 결과가 업데이트되었습니다.";
            default -> "지원 상태가 업데이트되었습니다.";
        };
        notificationService.createNotification(application.getUser().getId(), message, "APPLICATION_STATUS");

        return toEmployerDto(application);
    }

    private EmployerApplicationDto toEmployerDto(Application app) {
        EmployerApplicationDto dto = new EmployerApplicationDto();
        dto.setId(app.getId());
        dto.setJobId(app.getJobPosting().getId());
        dto.setJobTitle(app.getJobPosting().getTitle());
        dto.setApplicantId(app.getUser().getId());
        dto.setApplicantEmail(app.getUser().getEmail());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setPhone(app.getPhone());
        dto.setEmail(app.getEmail());
        dto.setStatus(app.getStatus());
        dto.setDisplayStatus(toDisplayStatus(app.getStatus()));
        dto.setAppliedAt(app.getAppliedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        resumeRepository.findByUser_Id(app.getUser().getId()).ifPresent(resume -> {
            ResumeDto resumeDto = new ResumeDto();
            resumeDto.setId(resume.getId());
            resumeDto.setUserId(resume.getUser().getId());
            resumeDto.setTitle(resume.getTitle());
            resumeDto.setSummary(resume.getSummary());
            resumeDto.setExperience(resume.getExperience());
            resumeDto.setEducation(resume.getEducation());
            resumeDto.setSkills(resume.getSkills());
            resumeDto.setCertifications(resume.getCertifications());
            resumeDto.setResumeFileUrl(resume.getResumeFileUrl());
            resumeDto.setCreatedAt(resume.getCreatedAt());
            resumeDto.setUpdatedAt(resume.getUpdatedAt());
            dto.setResume(resumeDto);
        });

        return dto;
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
        dto.setJob(toJobCardDto(app.getJobPosting()));
        dto.setStatus(app.getStatus());
        dto.setDisplayStatus(toDisplayStatus(app.getStatus()));
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
        dto.setExpiresAt(j.getExpiresAt());
        return dto;
    }

    // 구직자 뷰: "Application submitted" 필터 → PENDING + REVIEWING 모두 포함
    private List<String> normalizeStatusToList(String status) {
        if (status == null || status.isBlank() || "All".equalsIgnoreCase(status)) return null;

        return switch (status.trim().toUpperCase()) {
            case "APPLICATION SUBMITTED", "APPLIED", "PENDING", "REVIEWING" -> List.of("PENDING", "REVIEWING");
            case "PASSED", "ACCEPTED" -> List.of("ACCEPTED");
            case "FAILED", "REJECTED" -> List.of("REJECTED");
            case "WITHDRAWN" -> List.of("WITHDRAWN");
            default -> null;
        };
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank() || "All".equalsIgnoreCase(status)) return null;

        return switch (status.trim().toUpperCase()) {
            case "APPLICATION SUBMITTED", "APPLIED", "PENDING", "REVIEWING" -> "PENDING";
            case "PASSED", "ACCEPTED" -> "ACCEPTED";
            case "FAILED", "REJECTED" -> "REJECTED";
            case "WITHDRAWN" -> "WITHDRAWN";
            default -> status.trim().toUpperCase();
        };
    }

    // 구인자 뷰: 실제 DB 상태값 그대로 사용
    private String normalizeEmployerStatus(String status) {
        if (status == null || status.isBlank() || "All".equalsIgnoreCase(status)) {
            return null;
        }

        return switch (status.trim().toUpperCase()) {
            case "PENDING" -> "PENDING";
            case "REVIEWING" -> "REVIEWING";
            case "ACCEPTED", "PASSED" -> "ACCEPTED";
            case "REJECTED", "FAILED" -> "REJECTED";
            case "WITHDRAWN" -> "WITHDRAWN";
            default -> null;
        };
    }

    private String toDisplayStatus(String status) {
        if (status == null) {
            return "Application submitted";
        }

        return switch (status) {
            case "ACCEPTED" -> "Passed";
            case "REJECTED" -> "Failed";
            case "WITHDRAWN" -> "Withdrawn";
            default -> "Application submitted";
        };
    }
}
