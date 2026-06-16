package com.jubilee.workit.controller;

import com.jubilee.workit.dto.MyWorkitSummaryDto;
import com.jubilee.workit.service.MyWorkitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-workit")
@Tag(name = "MyWorkit", description = "마이워킷 API")
public class MyWorkitController {

    private final MyWorkitService myWorkitService;

    public MyWorkitController(MyWorkitService myWorkitService) {
        this.myWorkitService = myWorkitService;
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "마이워킷 요약 조회", description = "지원 현황, 북마크 수 등 마이워킷 요약 정보를 반환합니다.")
    public MyWorkitSummaryDto getSummary(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return myWorkitService.getSummary(userId);
    }
}
