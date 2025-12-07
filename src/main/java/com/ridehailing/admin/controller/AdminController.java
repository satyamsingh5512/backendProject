package com.ridehailing.admin.controller;

import com.ridehailing.analytics.service.AnalyticsService;
import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.repository.RiderRepository;
import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.repository.TripRepository;
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

    private final AnalyticsService analyticsService;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final TripRepository tripRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        Map<String, Object> stats = analyticsService.getPlatformStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<Page<Driver>>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> drivers = driverRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/riders")
    public ResponseEntity<ApiResponse<Page<Rider>>> getAllRiders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rider> riders = riderRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(riders));
    }

    @GetMapping("/trips")
    public ResponseEntity<ApiResponse<Page<Trip>>> getAllTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Trip> trips = tripRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new com.ridehailing.common.exception.ResourceNotFoundException("Driver not found"));
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @GetMapping("/riders/{riderId}")
    public ResponseEntity<ApiResponse<Rider>> getRiderById(@PathVariable Long riderId) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new com.ridehailing.common.exception.ResourceNotFoundException("Rider not found"));
        return ResponseEntity.ok(ApiResponse.success(rider));
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<ApiResponse<Trip>> getTripById(@PathVariable Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new com.ridehailing.common.exception.ResourceNotFoundException("Trip not found"));
        return ResponseEntity.ok(ApiResponse.success(trip));
    }
}
