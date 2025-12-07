package com.ridehailing.trip.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDto {
    
    @NotNull(message = "Origin latitude is required")
    private Double originLatitude;
    
    @NotNull(message = "Origin longitude is required")
    private Double originLongitude;
    
    @NotNull(message = "Destination latitude is required")
    private Double destinationLatitude;
    
    @NotNull(message = "Destination longitude is required")
    private Double destinationLongitude;
}
