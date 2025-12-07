package com.ridehailing.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimateResponse {
    private BigDecimal estimatedFare;
    private BigDecimal distanceKm;
    private BigDecimal baseFare;
    private BigDecimal perKmRate;
    private BigDecimal surgeMultiplier;
}
