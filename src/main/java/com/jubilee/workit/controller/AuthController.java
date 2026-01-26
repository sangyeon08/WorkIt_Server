package com.jubilee.workit.controller;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/role")
    public AuthResponse selectRole(@RequestBody RoleSelectRequest request) {
        return authService.selectRole(request);
    }
}