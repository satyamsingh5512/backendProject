package com.ridehailing.driver.repository;

import com.ridehailing.driver.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByDriverId(Long driverId);
    boolean existsByPlateNumber(String plateNumber);
}
