package com.ridehailing.driver.dto;

import com.ridehailing.driver.domain.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileDto {
    private Long id;
    private Long userId;
    private String name;
    private String phoneNumber;
    private DriverStatus status;
    private Double rating;
    private Integer totalTrips;
    private LocalDateTime createdAt;
    private VehicleDto vehicle;
}
