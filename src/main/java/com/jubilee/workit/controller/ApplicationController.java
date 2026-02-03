package com.jubilee.workit.controller;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(value = "/jobs/{jobId}/apply",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationDto apply(
            @PathVariable Long jobId,
            @RequestBody ApplyRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.apply(jobId, userId, request);
    }

    @GetMapping(value = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<ApplicationDto> getMyApplications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.getMyApplications(userId, page, size);
    }

    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApplicationDetailDto getApplicationDetail(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return applicationService.getApplicationDetail(id, userId);
    }

    @DeleteMapping(value = "/applications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelApplication(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        applicationService.cancelApplication(id, userId);
    }
}
