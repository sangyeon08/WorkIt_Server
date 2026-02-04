package com.jubilee.workit.controller;

import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.JobDetailDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.service.JobService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // 근처 공고 검색
    @GetMapping(value = "/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<JobCardDto> nearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10.0") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getNearby(latitude, longitude, radiusKm, page, size);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JobDetailDto getJobDetail(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        return jobService.getJobDetail(id, userId);
    }

    @GetMapping(value = "/hot", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<JobCardDto> hot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getHotPostings(page, size);
    }

    @GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<JobCardDto> latest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getLatestPostings(page, size);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<JobCardDto> list(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.getPostings(filter, locationId, page, size);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<JobCardDto> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.search(q, page, size);
    }
}