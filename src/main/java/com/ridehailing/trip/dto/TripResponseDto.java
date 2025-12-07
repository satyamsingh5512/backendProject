package com.ridehailing.trip.dto;

import com.ridehailing.trip.domain.PaymentStatus;
import com.ridehailing.trip.domain.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {
    private Long id;
    private Long riderId;
    private Long driverId;
    private TripStatus status;
    private Double originLatitude;
    private Double originLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private BigDecimal distanceKm;
    private BigDecimal surgeMultiplier;
    private PaymentStatus paymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
}
