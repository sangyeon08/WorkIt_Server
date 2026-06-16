package com.jubilee.workit.controller;

import com.jubilee.workit.dto.BulkJobIdsRequest;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.dto.RecentlyViewedJobDto;
import com.jubilee.workit.service.RecentlyViewedJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recently-viewed")
@Tag(name = "RecentlyViewed", description = "최근 본 공고 API")
public class RecentlyViewedJobController {

    private final RecentlyViewedJobService recentlyViewedJobService;

    public RecentlyViewedJobController(RecentlyViewedJobService recentlyViewedJobService) {
        this.recentlyViewedJobService = recentlyViewedJobService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "최근 본 공고 목록 조회", description = "로그인한 사용자의 최근 본 공고 목록을 반환합니다.")
    public PageResponse<RecentlyViewedJobDto> getMyRecentlyViewed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return recentlyViewedJobService.getMyRecentlyViewed(userId, page, size);
    }

    @PostMapping(value = "/jobs/{jobId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "공고 열람 기록", description = "특정 공고를 최근 본 공고에 기록합니다.")
    @ApiResponse(responseCode = "201", description = "기록 성공")
    public void recordView(@Parameter(description = "공고 ID") @PathVariable Long jobId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        recentlyViewedJobService.recordView(userId, jobId);
    }

    @DeleteMapping(value = "/jobs/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "최근 본 공고 단건 삭제", description = "특정 공고를 최근 본 공고 목록에서 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public void removeByJobId(@Parameter(description = "공고 ID") @PathVariable Long jobId, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        recentlyViewedJobService.removeByJobId(userId, jobId);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "최근 본 공고 일괄 삭제", description = "여러 공고를 최근 본 공고 목록에서 일괄 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public void removeMany(@RequestBody BulkJobIdsRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        recentlyViewedJobService.removeManyByJobIds(userId, request.getJobIds());
    }
}
