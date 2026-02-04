package com.jubilee.workit.service;

import com.jubilee.workit.dto.ResumeCreateRequest;
import com.jubilee.workit.dto.ResumeDto;
import com.jubilee.workit.dto.ResumeUpdateRequest;
import com.jubilee.workit.entity.Resume;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.ResumeRepository;
import com.jubilee.workit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    // 내 이력서 조회
    public ResumeDto getMyResume(Long userId) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "이력서가 없습니다. 먼저 이력서를 작성해주세요."));
        return toDto(resume);
    }

    // 이력서 생성
    @Transactional
    public ResumeDto createResume(Long userId, ResumeCreateRequest request) {
        // 이미 이력서가 있는지 확인
        if (resumeRepository.existsByUser_Id(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 이력서가 존재합니다. 수정 API를 사용해주세요.");
        }

        // 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 이력서 생성
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setTitle(request.getTitle());
        resume.setSummary(request.getSummary());
        resume.setExperience(request.getExperience());
        resume.setEducation(request.getEducation());
        resume.setSkills(request.getSkills());
        resume.setCertifications(request.getCertifications());
        resume.setResumeFileUrl(request.getResumeFileUrl());

        Resume saved = resumeRepository.save(resume);
        return toDto(saved);
    }

    // 이력서 수정
    @Transactional
    public ResumeDto updateResume(Long userId, ResumeUpdateRequest request) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "이력서를 찾을 수 없습니다. 먼저 이력서를 작성해주세요."));

        // 필드 업데이트 (null이 아닌 값만)
        if (request.getTitle() != null) {
            resume.setTitle(request.getTitle());
        }
        if (request.getSummary() != null) {
            resume.setSummary(request.getSummary());
        }
        if (request.getExperience() != null) {
            resume.setExperience(request.getExperience());
        }
        if (request.getEducation() != null) {
            resume.setEducation(request.getEducation());
        }
        if (request.getSkills() != null) {
            resume.setSkills(request.getSkills());
        }
        if (request.getCertifications() != null) {
            resume.setCertifications(request.getCertifications());
        }
        if (request.getResumeFileUrl() != null) {
            resume.setResumeFileUrl(request.getResumeFileUrl());
        }

        Resume updated = resumeRepository.save(resume);
        return toDto(updated);
    }

    // 이력서 삭제
    @Transactional
    public void deleteResume(Long userId) {
        Resume resume = resumeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "이력서를 찾을 수 없습니다."));

        resumeRepository.delete(resume);
    }

    // DTO 변환
    private ResumeDto toDto(Resume resume) {
        ResumeDto dto = new ResumeDto();
        dto.setId(resume.getId());
        dto.setUserId(resume.getUser().getId());
        dto.setTitle(resume.getTitle());
        dto.setSummary(resume.getSummary());
        dto.setExperience(resume.getExperience());
        dto.setEducation(resume.getEducation());
        dto.setSkills(resume.getSkills());
        dto.setCertifications(resume.getCertifications());
        dto.setResumeFileUrl(resume.getResumeFileUrl());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setUpdatedAt(resume.getUpdatedAt());
        return dto;
    }
}