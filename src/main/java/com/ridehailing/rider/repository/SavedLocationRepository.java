package com.ridehailing.rider.repository;

import com.ridehailing.rider.domain.SavedLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedLocationRepository extends JpaRepository<SavedLocation, Long> {
    List<SavedLocation> findByRiderId(Long riderId);
}
