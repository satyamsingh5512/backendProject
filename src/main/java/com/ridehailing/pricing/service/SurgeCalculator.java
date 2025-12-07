package com.ridehailing.pricing.service;

import com.ridehailing.driver.domain.DriverStatus;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates dynamic surge pricing based on supply and demand.
 * In production, this would be more sophisticated with:
 * - Real-time geospatial analysis
 * - Historical demand patterns
 * - Time-of-day factors
 * - Special events detection
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SurgeCalculator {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;

    @Value("${app.pricing.surge.enabled:true}")
    private boolean surgeEnabled;

    @Value("${app.pricing.surge.max-multiplier:3.0}")
    private double maxSurgeMultiplier;

    public BigDecimal calculateSurge(double latitude, double longitude) {
        if (!surgeEnabled) {
            return BigDecimal.ONE;
        }

        // Count active trip requests
        long activeRequests = tripRepository.countActiveRequests();
        
        // Count online drivers
        long onlineDrivers = driverRepository.findByStatus(DriverStatus.ONLINE).size();
        
        // Calculate demand/supply ratio
        double demandSupplyRatio = onlineDrivers > 0 
                ? (double) activeRequests / onlineDrivers 
                : activeRequests;
        
        // Calculate surge multiplier
        // Simple formula: 1.0 + (ratio * 0.5), capped at maxSurgeMultiplier
        double surge = 1.0 + (demandSupplyRatio * 0.5);
        surge = Math.min(surge, maxSurgeMultiplier);
        
        BigDecimal surgeMultiplier = BigDecimal.valueOf(surge)
                .setScale(2, RoundingMode.HALF_UP);
        
        log.debug("Surge calculation: requests={}, drivers={}, ratio={}, surge={}x",
                activeRequests, onlineDrivers, demandSupplyRatio, surgeMultiplier);
        
        return surgeMultiplier;
    }
}
