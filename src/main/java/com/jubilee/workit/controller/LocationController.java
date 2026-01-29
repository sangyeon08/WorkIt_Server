package com.jubilee.workit.controller;

import com.jubilee.workit.dto.LocationDto;
import com.jubilee.workit.service.LocationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocationDto> list() {
        return locationService.listAll();
    }
}
