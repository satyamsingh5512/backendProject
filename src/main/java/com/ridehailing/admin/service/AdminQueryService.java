package com.ridehailing.admin.service;

import com.ridehailing.analytics.service.AnalyticsService;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.repository.RiderRepository;
import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Application service for admin read-only queries.
 * Keeps controllers thin and centralizes admin query orchestration.
 */
@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private final AnalyticsService analyticsService;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final TripRepository tripRepository;

    public Map<String, Object> getPlatformStats() {
        return analyticsService.getPlatformStats();
    }

    public Page<Driver> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable);
    }

    public Page<Rider> getAllRiders(Pageable pageable) {
        return riderRepository.findAll(pageable);
    }

    public Page<Trip> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable);
    }

    public Driver getDriverById(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
    }

    public Rider getRiderById(Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found"));
    }

    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }
}
