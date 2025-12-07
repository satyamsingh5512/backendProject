package com.ridehailing.trip.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_rider_id", columnList = "rider_id"),
    @Index(name = "idx_trip_driver_id", columnList = "driver_id"),
    @Index(name = "idx_trip_status", columnList = "status"),
    @Index(name = "idx_trip_requested_at", columnList = "requested_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(name = "driver_id")
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TripStatus status = TripStatus.REQUESTED;

    @Column(name = "origin_latitude", nullable = false)
    private Double originLatitude;

    @Column(name = "origin_longitude", nullable = false)
    private Double originLongitude;

    @Column(name = "destination_latitude", nullable = false)
    private Double destinationLatitude;

    @Column(name = "destination_longitude", nullable = false)
    private Double destinationLongitude;

    @Column(name = "estimated_fare", precision = 10, scale = 2)
    private BigDecimal estimatedFare;

    @Column(name = "final_fare", precision = 10, scale = 2)
    private BigDecimal finalFare;

    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "surge_multiplier", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal surgeMultiplier = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreatedDate
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
