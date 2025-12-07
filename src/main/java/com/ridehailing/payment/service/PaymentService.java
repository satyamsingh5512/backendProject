package com.ridehailing.payment.service;

import com.ridehailing.common.exception.BusinessException;
import com.ridehailing.common.exception.ResourceNotFoundException;
import com.ridehailing.payment.domain.DriverEarning;
import com.ridehailing.payment.domain.Payment;
import com.ridehailing.payment.repository.DriverEarningRepository;
import com.ridehailing.payment.repository.PaymentRepository;
import com.ridehailing.trip.domain.PaymentStatus;
import com.ridehailing.trip.domain.Trip;
import com.ridehailing.trip.domain.TripStatus;
import com.ridehailing.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for processing payments and calculating driver earnings.
 * In production, this would integrate with real payment gateways like Stripe, PayPal, etc.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DriverEarningRepository earningRepository;
    private final TripRepository tripRepository;

    @Value("${app.payment.commission-rate:20.0}")
    private double commissionRate;

    @Transactional
    public Payment processPayment(Long tripId, String paymentMethod) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.COMPLETED) {
            throw new BusinessException("Can only process payment for completed trips");
        }
        
        if (trip.getFinalFare() == null) {
            throw new BusinessException("Trip does not have a final fare");
        }
        
        // Check if payment already exists
        if (paymentRepository.findByTripId(tripId).isPresent()) {
            throw new BusinessException("Payment already processed for this trip");
        }
        
        // Simulate payment processing
        String transactionId = UUID.randomUUID().toString();
        
        Payment payment = Payment.builder()
                .tripId(tripId)
                .amount(trip.getFinalFare())
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .processedAt(LocalDateTime.now())
                .build();
        
        payment = paymentRepository.save(payment);
        log.info("Payment processed for trip {}: amount={}, method={}, txId={}", 
                tripId, trip.getFinalFare(), paymentMethod, transactionId);
        
        // Calculate and record driver earnings
        if (trip.getDriverId() != null) {
            recordDriverEarnings(trip, payment);
        }
        
        return payment;
    }

    @Transactional
    protected void recordDriverEarnings(Trip trip, Payment payment) {
        BigDecimal grossAmount = payment.getAmount();
        BigDecimal commissionRateBD = new BigDecimal(commissionRate);
        BigDecimal commissionAmount = grossAmount
                .multiply(commissionRateBD)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal netAmount = grossAmount.subtract(commissionAmount);
        
        DriverEarning earning = DriverEarning.builder()
                .driverId(trip.getDriverId())
                .tripId(trip.getId())
                .grossAmount(grossAmount)
                .commissionRate(commissionRateBD)
                .commissionAmount(commissionAmount)
                .netAmount(netAmount)
                .payoutStatus("PENDING")
                .build();
        
        earningRepository.save(earning);
        log.info("Driver earnings recorded for trip {}: gross={}, commission={}, net={}", 
                trip.getId(), grossAmount, commissionAmount, netAmount);
    }

    public Payment getPaymentByTripId(Long tripId) {
        return paymentRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for trip"));
    }

    public BigDecimal getDriverTotalEarnings(Long driverId) {
        BigDecimal total = earningRepository.calculateTotalEarnings(driverId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getDriverEarningsForPeriod(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = earningRepository.calculateEarningsForPeriod(driverId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
}
