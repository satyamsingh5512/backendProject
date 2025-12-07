package com.ridehailing.rating.controller;

import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.rating.dto.RatingRequestDto;
import com.ridehailing.rating.dto.RatingResponseDto;
import com.ridehailing.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/driver")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<ApiResponse<RatingResponseDto>> rateDriver(@Valid @RequestBody RatingRequestDto request) {
        RatingResponseDto rating = ratingService.rateDriver(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Driver rated successfully", rating));
    }

    @PostMapping("/rider")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<RatingResponseDto>> rateRider(@Valid @RequestBody RatingRequestDto request) {
        RatingResponseDto rating = ratingService.rateRider(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rider rated successfully", rating));
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    public ResponseEntity<ApiResponse<RatingResponseDto>> getRatingByTripId(@PathVariable Long tripId) {
        RatingResponseDto rating = ratingService.getRatingByTripId(tripId);
        return ResponseEntity.ok(ApiResponse.success(rating));
    }

    @GetMapping("/driver/received")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<RatingResponseDto>>> getDriverRatings() {
        List<RatingResponseDto> ratings = ratingService.getDriverRatings();
        return ResponseEntity.ok(ApiResponse.success(ratings));
    }
}
