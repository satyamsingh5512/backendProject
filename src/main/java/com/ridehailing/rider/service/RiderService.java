package com.ridehailing.rider.service;

import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.common.security.SecurityUtils;
import com.ridehailing.rider.domain.Rider;
import com.ridehailing.rider.domain.SavedLocation;
import com.ridehailing.rider.dto.RiderProfileResponse;
import com.ridehailing.rider.dto.SavedLocationDto;
import com.ridehailing.rider.repository.RiderRepository;
import com.ridehailing.rider.repository.SavedLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderService {

    private final RiderRepository riderRepository;
    private final SavedLocationRepository savedLocationRepository;

    public RiderProfileResponse getCurrentRiderProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        Rider rider = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider profile not found"));
        
        return mapToProfileResponse(rider);
    }

    public Rider getCurrentRider() {
        Long userId = SecurityUtils.getCurrentUserId();
        return riderRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider profile not found"));
    }

    @Transactional
    public SavedLocationDto saveFavoriteLocation(SavedLocationDto dto) {
        Rider rider = getCurrentRider();
        
        SavedLocation location = SavedLocation.builder()
                .riderId(rider.getId())
                .label(dto.getLabel())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .address(dto.getAddress())
                .build();
        
        location = savedLocationRepository.save(location);
        log.info("Saved location created for rider: {}", rider.getId());
        
        return mapToLocationDto(location);
    }

    public List<SavedLocationDto> getSavedLocations() {
        Rider rider = getCurrentRider();
        return savedLocationRepository.findByRiderId(rider.getId())
                .stream()
                .map(this::mapToLocationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSavedLocation(Long locationId) {
        Rider rider = getCurrentRider();
        SavedLocation location = savedLocationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved location not found"));
        
        if (!location.getRiderId().equals(rider.getId())) {
            throw new ResourceNotFoundException("Saved location not found");
        }
        
        savedLocationRepository.delete(location);
        log.info("Deleted saved location: {} for rider: {}", locationId, rider.getId());
    }

    private RiderProfileResponse mapToProfileResponse(Rider rider) {
        return RiderProfileResponse.builder()
                .id(rider.getId())
                .userId(rider.getUserId())
                .name(rider.getName())
                .phoneNumber(rider.getPhoneNumber())
                .defaultPaymentMethod(rider.getDefaultPaymentMethod())
                .createdAt(rider.getCreatedAt())
                .build();
    }

    private SavedLocationDto mapToLocationDto(SavedLocation location) {
        return SavedLocationDto.builder()
                .id(location.getId())
                .label(location.getLabel())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .build();
    }
}
