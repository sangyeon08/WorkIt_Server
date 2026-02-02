package com.jubilee.workit.service;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.dto.ProfileDto;
import com.jubilee.workit.dto.ProfileUpdateRequest;
import com.jubilee.workit.entity.Location;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.LocationRepository;
import com.jubilee.workit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public ProfileService(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public ProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        ProfileDto dto = new ProfileDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setLoginType(user.getLoginType());
        dto.setPreferences(user.getPreferences());
        dto.setCreatedAt(user.getCreatedAt());

        // Location 정보
        if (user.getLocationId() != null) {
            locationRepository.findById(user.getLocationId()).ifPresent(location -> {
                LocationDto locationDto = new LocationDto();
                locationDto.setId(location.getId());
                locationDto.setName(location.getName());
                locationDto.setCity(location.getCity());
                locationDto.setCountry(location.getCountry());
                locationDto.setLatitude(location.getLatitude());
                locationDto.setLongitude(location.getLongitude());
                dto.setLocation(locationDto);
            });
        }

        return dto;
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // Location 업데이트
        if (request.getLocationId() != null) {
            if (!locationRepository.existsById(request.getLocationId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "존재하지 않는 지역입니다.");
            }
            user.setLocationId(request.getLocationId());
        }

        // Preferences 업데이트
        if (request.getPreferences() != null) {
            user.setPreferences(request.getPreferences());
        }

        userRepository.save(user);
        return getProfile(userId);
    }
}