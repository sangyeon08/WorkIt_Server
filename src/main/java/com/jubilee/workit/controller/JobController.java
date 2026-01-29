package com.jubilee.workit.controller;

import com.jubilee.workit.dto.JobCardDto;
import com.jubilee.workit.dto.PageResponse;
import com.jubilee.workit.service.JobService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
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
