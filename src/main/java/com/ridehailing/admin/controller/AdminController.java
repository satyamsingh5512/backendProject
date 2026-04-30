package com.ridehailing.admin.controller;

import com.ridehailing.admin.service.AdminQueryService;
import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.trip.domain.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin endpoints for platform management.
 * In production, implement proper admin authentication and authorization.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminQueryService adminQueryService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        Map<String, Object> stats = adminQueryService.getPlatformStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<Page<Driver>>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> drivers = adminQueryService.getAllDrivers(pageable);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/riders")
    public ResponseEntity<ApiResponse<Page<Rider>>> getAllRiders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rider> riders = adminQueryService.getAllRiders(pageable);
        return ResponseEntity.ok(ApiResponse.success(riders));
    }

    @GetMapping("/trips")
    public ResponseEntity<ApiResponse<Page<Trip>>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = adminQueryService.getAllTrips(pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable Long driverId) {
        Driver driver = adminQueryService.getDriverById(driverId);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @GetMapping("/riders/{riderId}")
    public ResponseEntity<ApiResponse<Rider>> getRiderById(@PathVariable Long riderId) {
        Rider rider = adminQueryService.getRiderById(riderId);
        return ResponseEntity.ok(ApiResponse.success(rider));
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<ApiResponse<Trip>> getTripById(@PathVariable Long tripId) {
        Trip trip = adminQueryService.getTripById(tripId);
        return ResponseEntity.ok(ApiResponse.success(trip));
    }
}
