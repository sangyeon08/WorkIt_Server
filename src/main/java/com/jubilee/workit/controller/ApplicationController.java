package com.jubilee.workit.controller;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Application", description = "지원 API")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(value = "/jobs/{jobId}/apply",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고 지원", description = "특정 공고에 지원합니다.")
    @ApiResponse(responseCode = "200", description = "지원 성공")
    public ApplicationDto apply(
            @Parameter(description = "공고 ID") @PathVariable Long jobId,
            @RequestBody ApplyRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.apply(jobId, userId, request);
    }

    @GetMapping(value = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "내 지원 목록 조회", description = "로그인한 사용자의 지원 목록을 조회합니다.")
    public PageResponse<ApplicationDto> getMyApplications(
            Authentication authentication,
            @Parameter(description = "지원 상태 필터 (예: PENDING, ACCEPTED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.getMyApplications(userId, status, q, page, size);
    }

    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "지원 상세 조회", description = "특정 지원의 상세 정보를 조회합니다.")
    public ApplicationDetailDto getApplicationDetail(
            @Parameter(description = "지원 ID") @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.getApplicationDetail(id, userId);
    }

    @DeleteMapping(value = "/applications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "지원 취소", description = "지원을 취소합니다.")
    @ApiResponse(responseCode = "204", description = "취소 성공")
    public void cancelApplication(
            @Parameter(description = "지원 ID") @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        applicationService.cancelApplication(id, userId);
    }
}
