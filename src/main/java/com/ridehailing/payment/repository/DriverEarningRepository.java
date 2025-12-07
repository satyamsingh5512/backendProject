package com.ridehailing.payment.repository;

import com.ridehailing.payment.domain.DriverEarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DriverEarningRepository extends JpaRepository<DriverEarning, Long> {
    
    List<DriverEarning> findByDriverIdOrderByCreatedAtDesc(Long driverId);
    
    List<DriverEarning> findByPayoutStatus(String payoutStatus);
    
    @Query("SELECT SUM(e.netAmount) FROM DriverEarning e WHERE e.driverId = :driverId")
    BigDecimal calculateTotalEarnings(@Param("driverId") Long driverId);
    
    @Query("SELECT SUM(e.netAmount) FROM DriverEarning e WHERE e.driverId = :driverId " +
           "AND e.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateEarningsForPeriod(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
