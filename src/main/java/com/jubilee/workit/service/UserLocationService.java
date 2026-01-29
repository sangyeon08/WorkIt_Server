package com.jubilee.workit.service;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.LocationRepository;
import com.jubilee.workit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserLocationService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public UserLocationService(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public LocationDto getLocation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        if (user.getLocationId() == null) return null;
        return locationRepository.findById(user.getLocationId())
                .map(loc -> {
                    LocationDto dto = new LocationDto();
                    dto.setId(loc.getId());
                    dto.setName(loc.getName());
                    dto.setCity(loc.getCity());
                    dto.setCountry(loc.getCountry());
                    dto.setLatitude(loc.getLatitude());
                    dto.setLongitude(loc.getLongitude());
                    return dto;
                })
                .orElse(null);
    }

    public void setLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        if (locationId != null && !locationRepository.existsById(locationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 지역입니다.");
        }
        user.setLocationId(locationId);
        userRepository.save(user);
    }
}
