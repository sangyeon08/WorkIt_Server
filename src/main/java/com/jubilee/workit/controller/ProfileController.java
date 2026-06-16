package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ProfileDto;
import com.jubilee.workit.dto.ProfileUpdateRequest;
import com.jubilee.workit.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
@Tag(name = "Profile", description = "프로필 API")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "프로필 조회", description = "로그인한 사용자의 프로필 정보를 반환합니다.")
    public ProfileDto getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return profileService.getProfile(userId);
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "프로필 수정", description = "프로필 정보를 수정합니다.")
    public ProfileDto updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return profileService.updateProfile(userId, request);
    }
}
