package com.ridehailing.rating.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequestDto {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
    private Double rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
