package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ResumeCreateRequest;
import com.jubilee.workit.dto.ResumeDto;
import com.jubilee.workit.dto.ResumeUpdateRequest;
import com.jubilee.workit.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume", description = "이력서 API")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // 내 이력서 조회
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "내 이력서 조회", description = "로그인한 사용자의 이력서를 반환합니다.")
    public ResumeDto getMyResume(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.getMyResume(userId);
    }

    // 이력서 생성
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이력서 생성", description = "새로운 이력서를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "이력서 생성 성공")
    public ResumeDto createResume(
            @RequestBody ResumeCreateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.createResume(userId, request);
    }

    // 이력서 수정
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이력서 수정", description = "기존 이력서를 수정합니다.")
    public ResumeDto updateResume(
            @RequestBody ResumeUpdateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.updateResume(userId, request);
    }

    // 이력서 삭제
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "이력서 삭제", description = "이력서를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public void deleteResume(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        resumeService.deleteResume(userId);
    }
}