package com.ridehailing.rating.repository;

import com.ridehailing.rating.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    Optional<Rating> findByTripId(Long tripId);
    
    List<Rating> findByRiderId(Long riderId);
    
    List<Rating> findByDriverId(Long driverId);
    
    @Query("SELECT AVG(r.driverRating) FROM Rating r WHERE r.driverId = :driverId AND r.driverRating IS NOT NULL")
    Double calculateAverageDriverRating(@Param("driverId") Long driverId);
    
    @Query("SELECT AVG(r.riderRating) FROM Rating r WHERE r.riderId = :riderId AND r.riderRating IS NOT NULL")
    Double calculateAverageRiderRating(@Param("riderId") Long riderId);
}
