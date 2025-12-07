package com.ridehailing.pricing.service;

import com.ridehailing.pricing.dto.PriceEstimateRequest;
import com.ridehailing.pricing.dto.PriceEstimateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating trip pricing with surge multiplier.
 * Implements a simple but realistic pricing model.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final SurgeCalculator surgeCalculator;

    @Value("${app.pricing.base-fare:2.50}")
    private double baseFare;

    @Value("${app.pricing.per-km-rate:1.20}")
    private double perKmRate;

    public PriceEstimateResponse calculatePrice(PriceEstimateRequest request) {
        double distanceKm = request.getDistanceKm();
        
        // Calculate surge multiplier based on demand
        BigDecimal surgeMultiplier = surgeCalculator.calculateSurge(
                request.getOriginLatitude(),
                request.getOriginLongitude()
        );
        
        // Base calculation: baseFare + (distanceKm * perKmRate)
        double basePrice = baseFare + (distanceKm * perKmRate);
        
        // Apply surge
        BigDecimal finalPrice = BigDecimal.valueOf(basePrice)
                .multiply(surgeMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
        
        log.info("Price calculated: distance={}km, base=${}, surge={}x, final=${}",
                distanceKm, basePrice, surgeMultiplier, finalPrice);
        
        return PriceEstimateResponse.builder()
                .estimatedFare(finalPrice)
                .distanceKm(BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP))
                .baseFare(BigDecimal.valueOf(baseFare))
                .perKmRate(BigDecimal.valueOf(perKmRate))
                .surgeMultiplier(surgeMultiplier)
                .build();
    }
}
