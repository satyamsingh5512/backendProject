package com.ridehailing.rider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phoneNumber;
    private String defaultPaymentMethod;
    private LocalDateTime createdAt;
}
