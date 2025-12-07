package com.ridehailing.analytics.controller;

import com.ridehailing.analytics.service.AnalyticsService;
import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.driver.service.DriverService;
import com.ridehailing.rider.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final DriverService driverService;
    private final RiderService riderService;

    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        Map<String, Object> stats = analyticsService.getPlatformStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDriverStats() {
        Long driverId = driverService.getCurrentDriver().getId();
        Map<String, Object> stats = analyticsService.getDriverStats(driverId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/rider")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiderStats() {
        Long riderId = riderService.getCurrentRider().getId();
        Map<String, Object> stats = analyticsService.getRiderStats(riderId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
