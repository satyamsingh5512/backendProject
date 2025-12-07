package com.ridehailing.driver.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Redis model for fast driver location lookups.
 * Stored with key pattern: driver:{driverId}:location
 */
@RedisHash(value = "driver_location", timeToLive = 3600) // 1 hour TTL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocation implements Serializable {

    @Id
    private Long driverId;

    @Indexed
    private Double latitude;

    @Indexed
    private Double longitude;

    @Indexed
    private DriverStatus status;

    private LocalDateTime lastUpdatedAt;
}
