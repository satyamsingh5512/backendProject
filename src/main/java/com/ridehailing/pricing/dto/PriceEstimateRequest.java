package com.ridehailing.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimateRequest {
    private double originLatitude;
    private double originLongitude;
    private double destinationLatitude;
    private double destinationLongitude;
    private double distanceKm;
}
