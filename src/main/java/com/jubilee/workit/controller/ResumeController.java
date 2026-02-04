package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ResumeCreateRequest;
import com.jubilee.workit.dto.ResumeDto;
import com.jubilee.workit.dto.ResumeUpdateRequest;
import com.jubilee.workit.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // 내 이력서 조회
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResumeDto getMyResume(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.getMyResume(userId);
    }

    // 이력서 생성
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeDto createResume(
            @RequestBody ResumeCreateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.createResume(userId, request);
    }

    // 이력서 수정
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResumeDto updateResume(
            @RequestBody ResumeUpdateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return resumeService.updateResume(userId, request);
    }

    // 이력서 삭제
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        resumeService.deleteResume(userId);
    }
}