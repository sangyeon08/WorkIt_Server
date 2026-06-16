package com.jubilee.workit.controller;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원가입", description = "이메일/비밀번호로 신규 계정을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "JWT 토큰 반환")
    public AuthResponse signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "JWT 토큰 반환")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping(value = "/role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "역할 선택", description = "소셜 로그인 후 구직자/고용주 역할을 선택합니다.")
    @ApiResponse(responseCode = "200", description = "갱신된 JWT 토큰 반환")
    public AuthResponse selectRole(@RequestBody RoleSelectRequest request) {
        return authService.selectRole(request);
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.")
    public AuthResponse me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return authService.getMe(userId);
    }

    @PostMapping(value = "/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "구글 소셜 로그인", description = "Google ID 토큰으로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "JWT 토큰 반환")
    public AuthResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.googleLogin(request);
    }

    @PostMapping(value = "/apple", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Apple 소셜 로그인", description = "Apple identity 토큰으로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "JWT 토큰 반환")
    public AuthResponse appleLogin(@RequestBody AppleLoginRequest request) {
        return authService.appleLogin(request);
    }
}
