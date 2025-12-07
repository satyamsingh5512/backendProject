package com.ridehailing.pricing.controller;

import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.pricing.dto.PriceEstimateRequest;
import com.ridehailing.pricing.dto.PriceEstimateResponse;
import com.ridehailing.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/estimate")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    public ResponseEntity<ApiResponse<PriceEstimateResponse>> getPriceEstimate(
            @Valid @RequestBody PriceEstimateRequest request) {
        PriceEstimateResponse estimate = pricingService.calculatePrice(request);
        return ResponseEntity.ok(ApiResponse.success("Price estimated", estimate));
    }
}
