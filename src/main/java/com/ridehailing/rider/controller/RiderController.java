package com.ridehailing.rider.controller;

import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.rider.dto.RiderProfileResponse;
import com.ridehailing.rider.dto.SavedLocationDto;
import com.ridehailing.rider.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RIDER')")
public class RiderController {

    private final RiderService riderService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<RiderProfileResponse>> getProfile() {
        RiderProfileResponse profile = riderService.getCurrentRiderProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/locations")
    public ResponseEntity<ApiResponse<SavedLocationDto>> saveLocation(
            @Valid @RequestBody SavedLocationDto request) {
        SavedLocationDto location = riderService.saveFavoriteLocation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location saved successfully", location));
    }

    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<SavedLocationDto>>> getSavedLocations() {
        List<SavedLocationDto> locations = riderService.getSavedLocations();
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long locationId) {
        riderService.deleteSavedLocation(locationId);
        return ResponseEntity.ok(ApiResponse.success("Location deleted successfully", null));
    }
}
