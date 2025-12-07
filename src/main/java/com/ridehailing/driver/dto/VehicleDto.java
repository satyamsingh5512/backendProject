package com.ridehailing.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private Long id;
    
    @NotBlank(message = "Plate number is required")
    private String plateNumber;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotBlank(message = "Color is required")
    private String color;
    
    @NotNull(message = "Year is required")
    private Integer year;
}
