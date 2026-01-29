package com.jubilee.workit.controller;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.dto.LocationUpdateRequest;
import com.jubilee.workit.service.UserLocationService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserLocationService userLocationService;

    public UserController(UserLocationService userLocationService) {
        this.userLocationService = userLocationService;
    }

    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationDto getLocation(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return userLocationService.getLocation(userId);
    }

    @PatchMapping(value = "/location", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateLocation(Authentication auth, @RequestBody LocationUpdateRequest req) {
        Long userId = (Long) auth.getPrincipal();
        userLocationService.setLocation(userId, req.getLocationId());
    }
}
