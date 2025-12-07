package com.ridehailing.trip.repository;

import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.domain.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    Page<Trip> findByRiderIdOrderByRequestedAtDesc(Long riderId, Pageable pageable);
    
    Page<Trip> findByDriverIdOrderByRequestedAtDesc(Long driverId, Pageable pageable);
    
    List<Trip> findByStatus(TripStatus status);
    
    @Query("SELECT t FROM Trip t WHERE t.riderId = :riderId AND t.status IN ('REQUESTED', 'ACCEPTED', 'IN_PROGRESS')")
    Optional<Trip> findActiveTrip(@Param("riderId") Long riderId);
    
    @Query("SELECT t FROM Trip t WHERE t.driverId = :driverId AND t.status IN ('ACCEPTED', 'IN_PROGRESS')")
    Optional<Trip> findActiveDriverTrip(@Param("driverId") Long driverId);
    
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = 'REQUESTED'")
    long countActiveRequests();
    
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = 'REQUESTED' " +
           "AND t.originLatitude BETWEEN :minLat AND :maxLat " +
           "AND t.originLongitude BETWEEN :minLon AND :maxLon")
    long countRequestsInArea(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon
    );
}
