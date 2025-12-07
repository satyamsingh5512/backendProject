package com.ridehailing.rating.service;

import com.ridehailing.common.exception.BusinessException;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.driver.service.DriverService;
import com.ridehailing.rating.domain.Rating;
import com.ridehailing.rating.dto.RatingRequestDto;
import com.ridehailing.rating.dto.RatingResponseDto;
import com.ridehailing.rating.repository.RatingRepository;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.service.RiderService;
import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.domain.TripStatus;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final RiderService riderService;
    private final DriverService driverService;

    @Transactional
    public RatingResponseDto rateDriver(RatingRequestDto request) {
        Rider rider = riderService.getCurrentRider();
        
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        // Validate trip belongs to rider
        if (!trip.getRiderId().equals(rider.getId())) {
            throw new BusinessException("Not authorized to rate this trip");
        }
        
        // Validate trip is completed
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new BusinessException("Can only rate completed trips");
        }
        
        if (trip.getDriverId() == null) {
            throw new BusinessException("Trip has no assigned driver");
        }
        
        // Check if rating already exists
        Rating rating = ratingRepository.findByTripId(trip.getId())
                .orElseGet(() -> Rating.builder()
                        .tripId(trip.getId())
                        .riderId(rider.getId())
                        .driverId(trip.getDriverId())
                        .build());
        
        if (rating.getDriverRating() != null) {
            throw new BusinessException("Driver already rated for this trip");
        }
        
        // Set driver rating
        rating.setDriverRating(request.getRating());
        rating.setDriverComment(request.getComment());
        rating = ratingRepository.save(rating);
        
        // Update driver's average rating
        updateDriverAverageRating(trip.getDriverId());
        
        log.info("Driver {} rated {} by rider {} for trip {}", 
                trip.getDriverId(), request.getRating(), rider.getId(), trip.getId());
        
        return mapToResponseDto(rating);
    }

    @Transactional
    public RatingResponseDto rateRider(RatingRequestDto request) {
        Driver driver = driverService.getCurrentDriver();
        
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        // Validate trip belongs to driver
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new BusinessException("Not authorized to rate this trip");
        }
        
        // Validate trip is completed
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new BusinessException("Can only rate completed trips");
        }
        
        // Check if rating already exists
        Rating rating = ratingRepository.findByTripId(trip.getId())
                .orElseGet(() -> Rating.builder()
                        .tripId(trip.getId())
                        .riderId(trip.getRiderId())
                        .driverId(driver.getId())
                        .build());
        
        if (rating.getRiderRating() != null) {
            throw new BusinessException("Rider already rated for this trip");
        }
        
        // Set rider rating
        rating.setRiderRating(request.getRating());
        rating.setRiderComment(request.getComment());
        rating = ratingRepository.save(rating);
        
        log.info("Rider {} rated {} by driver {} for trip {}", 
                trip.getRiderId(), request.getRating(), driver.getId(), trip.getId());
        
        return mapToResponseDto(rating);
    }

    public RatingResponseDto getRatingByTripId(Long tripId) {
        Rating rating = ratingRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for trip"));
        
        return mapToResponseDto(rating);
    }

    public List<RatingResponseDto> getDriverRatings() {
        Driver driver = driverService.getCurrentDriver();
        return ratingRepository.findByDriverId(driver.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Double getDriverAverageRating(Long driverId) {
        Double avg = ratingRepository.calculateAverageDriverRating(driverId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0;
    }

    @Transactional
    protected void updateDriverAverageRating(Long driverId) {
        Double newAverage = getDriverAverageRating(driverId);
        driverRepository.findById(driverId).ifPresent(driver -> {
            driver.setRating(newAverage);
            driverRepository.save(driver);
            log.debug("Updated driver {} average rating to {}", driverId, newAverage);
        });
    }

    private RatingResponseDto mapToResponseDto(Rating rating) {
        return RatingResponseDto.builder()
                .id(rating.getId())
                .tripId(rating.getTripId())
                .riderId(rating.getRiderId())
                .driverId(rating.getDriverId())
                .riderRating(rating.getRiderRating())
                .driverRating(rating.getDriverRating())
                .riderComment(rating.getRiderComment())
                .driverComment(rating.getDriverComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
