package com.ridehailing.analytics.service;

import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.payment.repository.DriverEarningRepository;
import com.ridehailing.rating.repository.RatingRepository;
import com.ridehailing.rider.repository.RiderRepository;
import com.ridehailing.trip.domain.TripStatus;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating platform analytics and statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final DriverEarningRepository earningRepository;
    private final RatingRepository ratingRepository;

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User counts
        stats.put("totalDrivers", driverRepository.count());
        stats.put("totalRiders", riderRepository.count());
        stats.put("onlineDrivers", driverRepository.findByStatus(
                com.ridehailing.driver.domain.DriverStatus.ONLINE).size());
        
        // Trip stats
        stats.put("totalTrips", tripRepository.count());
        stats.put("completedTrips", tripRepository.findByStatus(TripStatus.COMPLETED).size());
        stats.put("activeTrips", tripRepository.findByStatus(TripStatus.IN_PROGRESS).size());
        stats.put("requestedTrips", tripRepository.findByStatus(TripStatus.REQUESTED).size());
        
        // Revenue stats
        stats.put("totalPlatformRevenue", calculateTotalRevenue());
        
        log.info("Platform stats generated: {}", stats);
        return stats;
    }

    public Map<String, Object> getDriverStats(Long driverId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic info
        driverRepository.findById(driverId).ifPresent(driver -> {
            stats.put("totalTrips", driver.getTotalTrips());
            stats.put("rating", driver.getRating());
        });
        
        // Earnings
        BigDecimal totalEarnings = earningRepository.calculateTotalEarnings(driverId);
        stats.put("totalEarnings", totalEarnings != null ? totalEarnings : BigDecimal.ZERO);
        
        // This week earnings
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        BigDecimal weeklyEarnings = earningRepository.calculateEarningsForPeriod(
                driverId, startOfWeek, LocalDateTime.now());
        stats.put("weeklyEarnings", weeklyEarnings != null ? weeklyEarnings : BigDecimal.ZERO);
        
        // Average rating
        Double avgRating = ratingRepository.calculateAverageDriverRating(driverId);
        stats.put("averageRating", avgRating != null ? avgRating : 5.0);
        
        return stats;
    }

    public Map<String, Object> getRiderStats(Long riderId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Trip count
        long tripCount = tripRepository.findByRiderIdOrderByRequestedAtDesc(
                riderId, org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        stats.put("totalTrips", tripCount);
        
        // Average rating given by rider
        Double avgRating = ratingRepository.calculateAverageRiderRating(riderId);
        stats.put("averageRating", avgRating != null ? avgRating : 5.0);
        
        return stats;
    }

    private BigDecimal calculateTotalRevenue() {
        // Sum all commission amounts
        return earningRepository.findAll().stream()
                .map(earning -> earning.getCommissionAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
