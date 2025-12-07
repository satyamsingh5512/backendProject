package com.ridehailing.trip.repository;

import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.domain.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    List<Trip> findByRiderIdOrderByRequestedAtDesc(Long riderId);
    
    List<Trip> findByDriverIdOrderByRequestedAtDesc(Long driverId);
    
    List<Trip> findByStatus(TripStatus status);
    
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
