package com.ridehailing.trip.service;

import com.ridehailing.driver.domain.DriverLocation;
import com.ridehailing.driver.service.DriverLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for matching riders with nearby available drivers.
 * In production, this would use more sophisticated algorithms:
 * - Driver acceptance rate
 * - Driver rating
 * - Estimated time to pickup
 * - Driver preferences
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverMatchingService {

    private final DriverLocationService driverLocationService;

    @Value("${app.driver.matching.max-drivers-to-check:10}")
    private int maxDriversToCheck;

    public Optional<Long> findNearestDriver(double latitude, double longitude) {
        List<DriverLocation> nearbyDrivers = driverLocationService.findNearbyDrivers(
                latitude, longitude, maxDriversToCheck
        );
        
        if (nearbyDrivers.isEmpty()) {
            log.warn("No available drivers found near ({}, {})", latitude, longitude);
            return Optional.empty();
        }
        
        // For simplicity, return the first driver
        // In production: sort by distance, rating, acceptance rate, etc.
        DriverLocation selectedDriver = nearbyDrivers.get(0);
        log.info("Matched driver {} for location ({}, {})", 
                selectedDriver.getDriverId(), latitude, longitude);
        
        return Optional.of(selectedDriver.getDriverId());
    }
}
