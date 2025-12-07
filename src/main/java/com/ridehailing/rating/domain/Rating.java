package com.ridehailing.rating.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", indexes = {
    @Index(name = "idx_rating_trip_id", columnList = "trip_id"),
    @Index(name = "idx_rating_rider_id", columnList = "rider_id"),
    @Index(name = "idx_rating_driver_id", columnList = "driver_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false, unique = true)
    private Long tripId;

    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "rider_rating", precision = 2, scale = 1)
    private Double riderRating;

    @Column(name = "driver_rating", precision = 2, scale = 1)
    private Double driverRating;

    @Column(name = "rider_comment", columnDefinition = "TEXT")
    private String riderComment;

    @Column(name = "driver_comment", columnDefinition = "TEXT")
    private String driverComment;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
