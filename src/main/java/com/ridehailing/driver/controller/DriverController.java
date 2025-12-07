package com.ridehailing.driver.controller;

import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.driver.dto.DriverProfileDto;
import com.ridehailing.driver.dto.LocationUpdateRequest;
import com.ridehailing.driver.dto.VehicleDto;
import com.ridehailing.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DriverProfileDto>> getProfile() {
        DriverProfileDto profile = driverService.getCurrentDriverProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/vehicle")
    public ResponseEntity<ApiResponse<VehicleDto>> registerVehicle(@Valid @RequestBody VehicleDto request) {
        VehicleDto vehicle = driverService.registerVehicle(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle registered successfully", vehicle));
    }

    @PostMapping("/status/online")
    public ResponseEntity<ApiResponse<Void>> goOnline() {
        driverService.goOnline();
        return ResponseEntity.ok(ApiResponse.success("Driver is now online", null));
    }

    @PostMapping("/status/offline")
    public ResponseEntity<ApiResponse<Void>> goOffline() {
        driverService.goOffline();
        return ResponseEntity.ok(ApiResponse.success("Driver is now offline", null));
    }

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        driverService.updateLocation(request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(ApiResponse.success("Location updated", null));
    }
}
