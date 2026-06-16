package com.jubilee.workit.controller;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.service.ApplicationService;
import com.jubilee.workit.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employer")
@Tag(name = "Employer", description = "구인자 API")
public class EmployerController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    public EmployerController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    // ── 공고 관리 ─────────────────────────────────────────────────────────────

    @GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "내 공고 목록", description = "구인자가 등록한 공고 목록을 조회합니다.")
    public PageResponse<JobCardDto> getMyJobs(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long employerId = (Long) auth.getPrincipal();
        return jobService.getMyPostedJobs(employerId, page, size);
    }

    @PostMapping(value = "/jobs",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공고 등록", description = "새 구인 공고를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "공고 등록 성공")
    public JobDetailDto createJob(
            Authentication auth,
            @RequestBody JobCreateRequest request) {
        Long employerId = (Long) auth.getPrincipal();
        return jobService.createJob(employerId, request);
    }

    @PutMapping(value = "/jobs/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고 수정", description = "등록한 공고를 수정합니다. null 필드는 변경되지 않습니다.")
    public JobDetailDto updateJob(
            @Parameter(description = "공고 ID") @PathVariable Long id,
            Authentication auth,
            @RequestBody JobUpdateRequest request) {
        Long employerId = (Long) auth.getPrincipal();
        return jobService.updateJob(id, employerId, request);
    }

    @DeleteMapping(value = "/jobs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "공고 삭제", description = "등록한 공고를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public void deleteJob(
            @Parameter(description = "공고 ID") @PathVariable Long id,
            Authentication auth) {
        Long employerId = (Long) auth.getPrincipal();
        jobService.deleteJob(id, employerId);
    }

    // ── 지원자 관리 ───────────────────────────────────────────────────────────

    @GetMapping(value = "/jobs/{jobId}/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고별 지원자 목록", description = "특정 공고에 지원한 지원자 목록을 조회합니다.")
    public PageResponse<EmployerApplicationDto> getJobApplications(
            @Parameter(description = "공고 ID") @PathVariable Long jobId,
            Authentication auth,
            @Parameter(description = "상태 필터 (PENDING, REVIEWING, ACCEPTED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "지원자 이메일 검색") @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long employerId = (Long) auth.getPrincipal();
        return applicationService.getJobApplications(jobId, employerId, status, q, page, size);
    }

    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "지원 상세 조회 (구인자)", description = "특정 지원의 상세 정보와 지원자 이력서를 조회합니다.")
    public EmployerApplicationDto getApplicationDetail(
            @Parameter(description = "지원 ID") @PathVariable Long id,
            Authentication auth) {
        Long employerId = (Long) auth.getPrincipal();
        return applicationService.getApplicationDetailForEmployer(id, employerId);
    }

    @PatchMapping(value = "/applications/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "지원 상태 변경", description = "지원자의 상태를 변경합니다. (REVIEWING, ACCEPTED, REJECTED)")
    public EmployerApplicationDto updateApplicationStatus(
            @Parameter(description = "지원 ID") @PathVariable Long id,
            Authentication auth,
            @RequestBody ApplicationStatusUpdateRequest request) {
        Long employerId = (Long) auth.getPrincipal();
        return applicationService.updateApplicationStatus(id, employerId, request.getStatus());
    }
}
