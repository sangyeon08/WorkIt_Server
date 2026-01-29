package com.jubilee.workit.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.UserRepository;
import com.jubilee.workit.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${workit.google.client-id:}")
    private String googleClientId;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLoginType(Optional.ofNullable(request.getLoginType()).orElse("EMAIL"));

        User savedUser = userRepository.save(user);
        String token = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail());
        long expiresIn = jwtUtil.getExpirationMs() / 1000;

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                null,
                "회원가입 성공! 역할을 선택해주세요.",
                token,
                expiresIn
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 이메일입니다."));

        if (user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "소셜 로그인으로 가입한 계정입니다. Google 또는 Apple로 로그인해주세요.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());
        long expiresIn = jwtUtil.getExpirationMs() / 1000;

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                "로그인 성공!",
                token,
                expiresIn
        );
    }

    public AuthResponse selectRole(RoleSelectRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String role = request.getRole();
        if (!"JOBSEEKER".equals(role) && !"EMPLOYER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 역할을 선택해주세요. (JOBSEEKER 또는 EMPLOYER)");
        }

        user.setRole(role);
        User updatedUser = userRepository.save(user);

        String roleMessage = "JOBSEEKER".equals(role) ? "구직자로 등록되었습니다!" : "구인자로 등록되었습니다!";

        return new AuthResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                roleMessage
        );
    }

    public AuthResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                null
        );
    }

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Google 로그인이 설정되지 않았습니다. workit.google.client-id를 설정해주세요.");
        }
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idToken이 필요합니다.");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google ID 토큰입니다.");
        }

        if (idToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google ID 토큰입니다.");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            email = googleId + "@google.workit.local";
        }
        final String lookupEmail = email;

        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(lookupEmail))
                .orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setGoogleId(googleId);
            user.setLoginType("GOOGLE");
            user.setPassword(null);
            user = userRepository.save(user);
        } else if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user.setLoginType("GOOGLE");
            user = userRepository.save(user);
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());
        long expiresIn = jwtUtil.getExpirationMs() / 1000;

        String message = user.getRole() == null ? "로그인 성공! 역할을 선택해주세요." : "로그인 성공!";

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                message,
                token,
                expiresIn
        );
    }

    public AuthResponse appleLogin(AppleLoginRequest request) {
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "Apple 로그인은 준비 중입니다. Apple identity_token 검증 및 JWKS 연동이 필요합니다."
        );
    }
}
