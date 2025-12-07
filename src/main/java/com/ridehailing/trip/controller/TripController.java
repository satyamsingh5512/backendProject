package com.ridehailing.trip.controller;

import com.ridehailing.common.dto.ApiResponse;
import com.ridehailing.trip.dto.TripRequestDto;
import com.ridehailing.trip.dto.TripResponseDto;
import com.ridehailing.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> requestTrip(@Valid @RequestBody TripRequestDto request) {
        TripResponseDto trip = tripService.requestTrip(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip requested successfully", trip));
    }

    @PostMapping("/{tripId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> acceptTrip(@PathVariable Long tripId) {
        TripResponseDto trip = tripService.acceptTrip(tripId);
        return ResponseEntity.ok(ApiResponse.success("Trip accepted", trip));
    }

    @PostMapping("/{tripId}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> startTrip(@PathVariable Long tripId) {
        TripResponseDto trip = tripService.startTrip(tripId);
        return ResponseEntity.ok(ApiResponse.success("Trip started", trip));
    }

    @PostMapping("/{tripId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> completeTrip(@PathVariable Long tripId) {
        TripResponseDto trip = tripService.completeTrip(tripId);
        return ResponseEntity.ok(ApiResponse.success("Trip completed", trip));
    }

    @PostMapping("/{tripId}/cancel")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> cancelTrip(
            @PathVariable Long tripId,
            @RequestParam(required = false, defaultValue = "User cancelled") String reason) {
        TripResponseDto trip = tripService.cancelTrip(tripId, reason);
        return ResponseEntity.ok(ApiResponse.success("Trip cancelled", trip));
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> getTripById(@PathVariable Long tripId) {
        TripResponseDto trip = tripService.getTripById(tripId);
        return ResponseEntity.ok(ApiResponse.success(trip));
    }

    @GetMapping("/rider/history")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<ApiResponse<List<TripResponseDto>>> getRiderTripHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<TripResponseDto> trips = tripService.getRiderTripHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/driver/history")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<TripResponseDto>>> getDriverTripHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<TripResponseDto> trips = tripService.getDriverTripHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success(trips));
    }

    @GetMapping("/rider/active")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> getCurrentActiveTrip() {
        TripResponseDto trip = tripService.getCurrentActiveTrip();
        return ResponseEntity.ok(ApiResponse.success(trip));
    }

    @GetMapping("/driver/active")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<TripResponseDto>> getDriverActiveTrip() {
        TripResponseDto trip = tripService.getDriverActiveTrip();
        return ResponseEntity.ok(ApiResponse.success(trip));
    }
}
