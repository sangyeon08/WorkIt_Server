package com.jubilee.workit.service;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.entity.Location;
import com.jubilee.workit.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<LocationDto> listAll() {
        return locationRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LocationDto toDto(Location loc) {
        LocationDto dto = new LocationDto();
        dto.setId(loc.getId());
        dto.setName(loc.getName());
        dto.setCity(loc.getCity());
        dto.setCountry(loc.getCountry());
        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());
        return dto;
    }
}
