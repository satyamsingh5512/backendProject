package com.ridehailing.rating.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDto {

    private Long id;
    private Long tripId;
    private Long riderId;
    private Long driverId;
    private Double riderRating;
    private Double driverRating;
    private String riderComment;
    private String driverComment;
    private LocalDateTime createdAt;
}
