package com.jubilee.workit.controller;

import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.JobDetailDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.service.JobService;
import com.jubilee.workit.service.RecentlyViewedJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job", description = "공고 API")
public class JobController {

    private final JobService jobService;
    private final RecentlyViewedJobService recentlyViewedJobService;

    public JobController(JobService jobService, RecentlyViewedJobService recentlyViewedJobService) {
        this.jobService = jobService;
        this.recentlyViewedJobService = recentlyViewedJobService;
    }

    // 근처 공고 검색
    @GetMapping(value = "/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "근처 공고 검색", description = "위도/경도 기준 반경 내 공고를 조회합니다.")
    public PageResponse<JobCardDto> nearby(
            @Parameter(description = "위도") @RequestParam double latitude,
            @Parameter(description = "경도") @RequestParam double longitude,
            @Parameter(description = "검색 반경(km), 기본값 10") @RequestParam(defaultValue = "10.0") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getNearby(latitude, longitude, radiusKm, page, size);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고 상세 조회", description = "공고 ID로 상세 정보를 조회합니다. 로그인 시 최근 본 공고에 기록됩니다.")
    public JobDetailDto getJobDetail(
            @Parameter(description = "공고 ID") @PathVariable Long id,
            Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        if (userId != null) {
            recentlyViewedJobService.recordView(userId, id);
        }
        return jobService.getJobDetail(id, userId);
    }

    @GetMapping(value = "/hot", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "인기 공고 목록", description = "조회수 기준 인기 공고를 조회합니다.")
    public PageResponse<JobCardDto> hot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getHotPostings(page, size);
    }

    @GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "최신 공고 목록", description = "최근 등록 순으로 공고를 조회합니다.")
    public PageResponse<JobCardDto> latest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getLatestPostings(page, size);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고 목록 조회", description = "필터 및 지역 조건으로 공고 목록을 조회합니다.")
    public PageResponse<JobCardDto> list(
            @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
            @Parameter(description = "지역 ID") @RequestParam(required = false) Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getPostings(filter, locationId, page, size);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "공고 검색", description = "키워드로 공고를 검색합니다.")
    public PageResponse<JobCardDto> search(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.search(q, page, size);
    }
}
