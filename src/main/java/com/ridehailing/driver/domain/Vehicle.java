package com.ridehailing.driver.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_driver_id", columnList = "driver_id"),
    @Index(name = "idx_vehicle_plate", columnList = "plate_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false, unique = true)
    private Long driverId;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false, length = 30)
    private String color;

    @Column(nullable = false)
    private Integer year;
}
