package com.jubilee.workit.controller;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location", description = "지역 API")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "지역 목록 조회", description = "전체 지역 목록을 반환합니다.")
    public List<LocationDto> list() {
        return locationService.listAll();
    }
}
