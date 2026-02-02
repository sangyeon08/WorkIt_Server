package com.jubilee.workit.controller;

import com.jubilee.workit.dto.ProfileDto;
import com.jubilee.workit.dto.ProfileUpdateRequest;
import com.jubilee.workit.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDto getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return profileService.getProfile(userId);
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDto updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return profileService.updateProfile(userId, request);
    }
}
