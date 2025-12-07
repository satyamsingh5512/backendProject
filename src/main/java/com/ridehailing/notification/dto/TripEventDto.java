package com.ridehailing.notification.dto;

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
public class TripEventDto {
    private Long tripId;
    private Long riderId;
    private Long driverId;
    private TripStatus status;
    private Double originLatitude;
    private Double originLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private LocalDateTime timestamp;
}
