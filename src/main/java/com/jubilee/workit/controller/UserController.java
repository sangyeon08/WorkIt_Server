package com.jubilee.workit.controller;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.dto.LocationUpdateRequest;
import com.jubilee.workit.service.UserLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserLocationService userLocationService;

    public UserController(UserLocationService userLocationService) {
        this.userLocationService = userLocationService;
    }

    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 위치 조회", description = "로그인한 사용자의 설정된 위치 정보를 반환합니다.")
    public LocationDto getLocation(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return userLocationService.getLocation(userId);
    }

    @PatchMapping(value = "/location", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 위치 수정", description = "사용자의 위치 정보를 수정합니다.")
    public void updateLocation(Authentication auth, @RequestBody LocationUpdateRequest req) {
        Long userId = (Long) auth.getPrincipal();
        userLocationService.setLocation(userId, req.getLocationId());
    }
}
