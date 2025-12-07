package com.ridehailing.rider.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_locations", indexes = {
    @Index(name = "idx_saved_location_rider_id", columnList = "rider_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rider_id", nullable = false)
    private Long riderId;

    @Column(nullable = false, length = 50)
    private String label; // e.g., "Home", "Work"

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 255)
    private String address;
}
