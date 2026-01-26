package com.jubilee.workit.service;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); //근데 암호화 해야함. 언젠간 하렴..
        user.setLoginType(request.getLoginType());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                null,
                "회원가입 성공! 역할을 선택해주세요."
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 이메일입니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                "로그인 성공!"
        );
    }

    public AuthResponse selectRole(RoleSelectRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!request.getRole().equals("JOBSEEKER") &&
                !request.getRole().equals("EMPLOYER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 역할을 선택해주세요.");
        }

        user.setRole(request.getRole());
        User updatedUser = userRepository.save(user);

        String roleMessage = request.getRole().equals("JOBSEEKER")
                ? "구직자로 등록되었습니다!"
                : "구인자로 등록되었습니다!";

        return new AuthResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                roleMessage
        );
    }
}