package com.ridehailing.driver.service;

import com.ridehailing.common.util.DistanceCalculator;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.domain.DriverLocation;
import com.ridehailing.driver.domain.DriverStatus;
import com.ridehailing.driver.repository.DriverLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing driver locations in Redis.
 * Provides fast geospatial queries for driver matching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverLocationService {

    private final DriverLocationRepository driverLocationRepository;

    @Value("${app.driver.matching.search-radius-km:5.0}")
    private double searchRadiusKm;

    public void updateDriverLocation(Driver driver, double latitude, double longitude) {
        DriverLocation location = DriverLocation.builder()
                .driverId(driver.getId())
                .latitude(latitude)
                .longitude(longitude)
                .status(driver.getStatus())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
        
        driverLocationRepository.save(location);
        log.debug("Updated location for driver: {} at ({}, {})", driver.getId(), latitude, longitude);
    }

    public Optional<DriverLocation> getDriverLocation(Long driverId) {
        return driverLocationRepository.findById(driverId);
    }

    public void removeDriverLocation(Long driverId) {
        driverLocationRepository.deleteById(driverId);
        log.debug("Removed location for driver: {}", driverId);
    }

    /**
     * Find nearby online drivers within search radius.
     * Note: This is a simple implementation. In production, use Redis GEO commands
     * or a dedicated geospatial database like PostGIS.
     */
    public List<DriverLocation> findNearbyDrivers(double latitude, double longitude, int maxDrivers) {
        List<DriverLocation> nearbyDrivers = new ArrayList<>();
        
        // Iterate through all driver locations (in production, use Redis GEO or spatial index)
        Iterable<DriverLocation> allLocations = driverLocationRepository.findAll();
        
        for (DriverLocation location : allLocations) {
            if (location.getStatus() == DriverStatus.ONLINE) {
                double distance = DistanceCalculator.calculateDistance(
                        latitude, longitude,
                        location.getLatitude(), location.getLongitude()
                );
                
                if (distance <= searchRadiusKm) {
                    nearbyDrivers.add(location);
                    
                    if (nearbyDrivers.size() >= maxDrivers) {
                        break;
                    }
                }
            }
        }
        
        log.info("Found {} nearby drivers within {}km of ({}, {})", 
                nearbyDrivers.size(), searchRadiusKm, latitude, longitude);
        
        return nearbyDrivers;
    }
}
