package com.ridehailing.driver.service;

import com.ridehailing.common.exception.BusinessException;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.common.security.SecurityUtils;
import com.ridehailing.driver.domain.Driver;
import com.ridehailing.driver.domain.DriverStatus;
import com.ridehailing.driver.domain.Vehicle;
import com.ridehailing.driver.dto.DriverProfileDto;
import com.ridehailing.driver.dto.VehicleDto;
import com.ridehailing.driver.repository.DriverRepository;
import com.ridehailing.driver.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverLocationService driverLocationService;

    public DriverProfileDto getCurrentDriverProfile() {
        Driver driver = getCurrentDriver();
        return mapToProfileDto(driver);
    }

    public Driver getCurrentDriver() {
        Long userId = SecurityUtils.getCurrentUserId();
        return driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));
    }

    @Transactional
    public VehicleDto registerVehicle(VehicleDto dto) {
        Driver driver = getCurrentDriver();
        
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new BusinessException("Vehicle with this plate number already registered");
        }
        
        Vehicle vehicle = Vehicle.builder()
                .driverId(driver.getId())
                .plateNumber(dto.getPlateNumber())
                .model(dto.getModel())
                .color(dto.getColor())
                .year(dto.getYear())
                .build();
        
        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle registered for driver: {}", driver.getId());
        
        return mapToVehicleDto(vehicle);
    }

    @Transactional
    public void goOnline() {
        Driver driver = getCurrentDriver();
        
        if (!vehicleRepository.findByDriverId(driver.getId()).isPresent()) {
            throw new BusinessException("Cannot go online without registering a vehicle");
        }
        
        driver.setStatus(DriverStatus.ONLINE);
        driverRepository.save(driver);
        log.info("Driver {} is now online", driver.getId());
    }

    @Transactional
    public void goOffline() {
        Driver driver = getCurrentDriver();
        driver.setStatus(DriverStatus.OFFLINE);
        driverRepository.save(driver);
        
        // Remove from Redis
        driverLocationService.removeDriverLocation(driver.getId());
        log.info("Driver {} is now offline", driver.getId());
    }

    @Transactional
    public void updateLocation(double latitude, double longitude) {
        Driver driver = getCurrentDriver();
        
        if (driver.getStatus() != DriverStatus.ONLINE && driver.getStatus() != DriverStatus.BUSY) {
            throw new BusinessException("Driver must be online to update location");
        }
        
        driverLocationService.updateDriverLocation(driver, latitude, longitude);
    }

    private DriverProfileDto mapToProfileDto(Driver driver) {
        VehicleDto vehicleDto = vehicleRepository.findByDriverId(driver.getId())
                .map(this::mapToVehicleDto)
                .orElse(null);
        
        return DriverProfileDto.builder()
                .id(driver.getId())
                .userId(driver.getUserId())
                .name(driver.getName())
                .phoneNumber(driver.getPhoneNumber())
                .status(driver.getStatus())
                .rating(driver.getRating())
                .totalTrips(driver.getTotalTrips())
                .createdAt(driver.getCreatedAt())
                .vehicle(vehicleDto)
                .build();
    }

    private VehicleDto mapToVehicleDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .year(vehicle.getYear())
                .build();
    }
}
