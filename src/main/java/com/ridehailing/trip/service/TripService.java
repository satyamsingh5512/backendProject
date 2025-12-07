package com.ridehailing.trip.service;

import com.ridehailing.common.exception.BusinessException;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.common.security.SecurityUtils;
import com.ridehailing.common.util.DistanceCalculator;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.domain.DriverStatus;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.driver.service.DriverService;
import com.ridehailing.notification.dto.TripEventDto;
import com.ridehailing.pricing.dto.PriceEstimateRequest;
import com.ridehailing.pricing.dto.PriceEstimateResponse;
import com.ridehailing.pricing.service.PricingService;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.service.RiderService;
import com.ridehailing.trip.domain.PaymentStatus;
import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.domain.TripStatus;
import com.ridehailing.trip.dto.TripRequestDto;
import com.ridehailing.trip.dto.TripResponseDto;
import com.ridehailing.trip.event.TripEventPublisher;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service for managing trip lifecycle:
 * 1. Request trip (rider)
 * 2. Match driver
 * 3. Accept trip (driver)
 * 4. Start trip (driver)
 * 5. Complete trip (driver)
 * 6. Cancel trip (rider/driver)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final RiderService riderService;
    private final DriverService driverService;
    private final PricingService pricingService;
    private final DriverMatchingService driverMatchingService;
    private final TripEventPublisher tripEventPublisher;

    @Transactional
    public TripResponseDto requestTrip(TripRequestDto request) {
        Rider rider = riderService.getCurrentRider();
        
        // Calculate distance
        double distanceKm = DistanceCalculator.calculateDistance(
                request.getOriginLatitude(),
                request.getOriginLongitude(),
                request.getDestinationLatitude(),
                request.getDestinationLongitude()
        );
        
        // Calculate price estimate
        PriceEstimateRequest priceRequest = PriceEstimateRequest.builder()
                .originLatitude(request.getOriginLatitude())
                .originLongitude(request.getOriginLongitude())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .distanceKm(distanceKm)
                .build();
        
        PriceEstimateResponse priceEstimate = pricingService.calculatePrice(priceRequest);
        
        // Create trip
        Trip trip = Trip.builder()
                .riderId(rider.getId())
                .status(TripStatus.REQUESTED)
                .originLatitude(request.getOriginLatitude())
                .originLongitude(request.getOriginLongitude())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .estimatedFare(priceEstimate.getEstimatedFare())
                .distanceKm(priceEstimate.getDistanceKm())
                .surgeMultiplier(priceEstimate.getSurgeMultiplier())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        
        Trip savedTrip = tripRepository.save(trip);
        log.info("Trip requested by rider: {}, trip id: {}", rider.getId(), savedTrip.getId());
        
        // Publish event
        tripEventPublisher.publishTripRequested(buildTripEventDto(savedTrip));
        
        // Try to match a driver
        driverMatchingService.findNearestDriver(
                request.getOriginLatitude(),
                request.getOriginLongitude()
        ).ifPresent(driverId -> {
            savedTrip.setDriverId(driverId);
            tripRepository.save(savedTrip);
            log.info("Auto-matched driver {} to trip {}", driverId, savedTrip.getId());
        });
        
        return mapToResponseDto(savedTrip);
    }

    @Transactional
    public TripResponseDto acceptTrip(Long tripId) {
        Driver driver = driverService.getCurrentDriver();
        
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.REQUESTED) {
            throw new BusinessException("Trip cannot be accepted in current status: " + trip.getStatus());
        }
        
        if (driver.getStatus() != DriverStatus.ONLINE) {
            throw new BusinessException("Driver must be online to accept trips");
        }
        
        // Update trip
        trip.setDriverId(driver.getId());
        trip.setStatus(TripStatus.ACCEPTED);
        trip.setAcceptedAt(LocalDateTime.now());
        tripRepository.save(trip);
        
        // Update driver status
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
        
        log.info("Trip {} accepted by driver {}", tripId, driver.getId());
        
        // Publish event
        tripEventPublisher.publishTripAccepted(buildTripEventDto(trip));
        
        return mapToResponseDto(trip);
    }

    @Transactional
    public TripResponseDto startTrip(Long tripId) {
        Driver driver = driverService.getCurrentDriver();
        
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new BusinessException("Driver not assigned to this trip");
        }
        
        if (trip.getStatus() != TripStatus.ACCEPTED) {
            throw new BusinessException("Trip must be accepted before starting");
        }
        
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartedAt(LocalDateTime.now());
        tripRepository.save(trip);
        
        log.info("Trip {} started by driver {}", tripId, driver.getId());
        
        // Publish event
        tripEventPublisher.publishTripStarted(buildTripEventDto(trip));
        
        return mapToResponseDto(trip);
    }

    @Transactional
    public TripResponseDto completeTrip(Long tripId) {
        Driver driver = driverService.getCurrentDriver();
        
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        if (!trip.getDriverId().equals(driver.getId())) {
            throw new BusinessException("Driver not assigned to this trip");
        }
        
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new BusinessException("Trip must be in progress to complete");
        }
        
        // Complete trip
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(LocalDateTime.now());
        trip.setFinalFare(trip.getEstimatedFare()); // In production, recalculate based on actual distance/time
        trip.setPaymentStatus(PaymentStatus.COMPLETED);
        tripRepository.save(trip);
        
        // Update driver stats
        driver.setStatus(DriverStatus.ONLINE);
        driver.setTotalTrips(driver.getTotalTrips() + 1);
        driverRepository.save(driver);
        
        log.info("Trip {} completed by driver {}", tripId, driver.getId());
        
        // Publish event
        tripEventPublisher.publishTripCompleted(buildTripEventDto(trip));
        
        return mapToResponseDto(trip);
    }

    @Transactional
    public TripResponseDto cancelTrip(Long tripId, String reason) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        // Verify user is either rider or driver
        Rider rider = riderService.getCurrentRider();
        boolean isRider = trip.getRiderId().equals(rider.getId());
        boolean isDriver = trip.getDriverId() != null && 
                driverRepository.findByUserId(userId).map(d -> d.getId().equals(trip.getDriverId())).orElse(false);
        
        if (!isRider && !isDriver) {
            throw new BusinessException("Not authorized to cancel this trip");
        }
        
        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel trip in current status: " + trip.getStatus());
        }
        
        trip.setStatus(TripStatus.CANCELLED);
        trip.setCancelledAt(LocalDateTime.now());
        
        // If driver was assigned, set them back to online
        if (trip.getDriverId() != null) {
            driverRepository.findById(trip.getDriverId()).ifPresent(driver -> {
                if (driver.getStatus() == DriverStatus.BUSY) {
                    driver.setStatus(DriverStatus.ONLINE);
                    driverRepository.save(driver);
                }
            });
        }
        
        tripRepository.save(trip);
        log.info("Trip {} cancelled. Reason: {}", tripId, reason);
        
        return mapToResponseDto(trip);
    }

    public TripResponseDto getTripById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        // Verify access
        Long userId = SecurityUtils.getCurrentUserId();
        Rider rider = riderService.getCurrentRider();
        boolean isRider = trip.getRiderId().equals(rider.getId());
        boolean isDriver = trip.getDriverId() != null && 
                driverRepository.findByUserId(userId).map(d -> d.getId().equals(trip.getDriverId())).orElse(false);
        
        if (!isRider && !isDriver) {
            throw new BusinessException("Not authorized to view this trip");
        }
        
        return mapToResponseDto(trip);
    }

    public List<TripResponseDto> getRiderTripHistory(Pageable pageable) {
        Rider rider = riderService.getCurrentRider();
        Page<Trip> trips = tripRepository.findByRiderIdOrderByRequestedAtDesc(rider.getId(), pageable);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<TripResponseDto> getDriverTripHistory(Pageable pageable) {
        Driver driver = driverService.getCurrentDriver();
        Page<Trip> trips = tripRepository.findByDriverIdOrderByRequestedAtDesc(driver.getId(), pageable);
        return trips.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public TripResponseDto getCurrentActiveTrip() {
        Rider rider = riderService.getCurrentRider();
        
        return tripRepository.findActiveTrip(rider.getId())
                .map(this::mapToResponseDto)
                .orElse(null);
    }

    public TripResponseDto getDriverActiveTrip() {
        Driver driver = driverService.getCurrentDriver();
        
        return tripRepository.findActiveDriverTrip(driver.getId())
                .map(this::mapToResponseDto)
                .orElse(null);
    }

    private TripResponseDto mapToResponseDto(Trip trip) {
        return TripResponseDto.builder()
                .id(trip.getId())
                .riderId(trip.getRiderId())
                .driverId(trip.getDriverId())
                .status(trip.getStatus())
                .originLatitude(trip.getOriginLatitude())
                .originLongitude(trip.getOriginLongitude())
                .destinationLatitude(trip.getDestinationLatitude())
                .destinationLongitude(trip.getDestinationLongitude())
                .estimatedFare(trip.getEstimatedFare())
                .finalFare(trip.getFinalFare())
                .distanceKm(trip.getDistanceKm())
                .surgeMultiplier(trip.getSurgeMultiplier())
                .paymentStatus(trip.getPaymentStatus())
                .requestedAt(trip.getRequestedAt())
                .acceptedAt(trip.getAcceptedAt())
                .startedAt(trip.getStartedAt())
                .completedAt(trip.getCompletedAt())
                .cancelledAt(trip.getCancelledAt())
                .build();
    }

    private TripEventDto buildTripEventDto(Trip trip) {
        return TripEventDto.builder()
                .tripId(trip.getId())
                .riderId(trip.getRiderId())
                .driverId(trip.getDriverId())
                .status(trip.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
