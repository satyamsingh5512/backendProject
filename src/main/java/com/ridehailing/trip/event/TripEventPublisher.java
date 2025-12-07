package com.ridehailing.trip.event;

import com.ridehailing.common.util.Constants;
import com.ridehailing.notification.dto.TripEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes trip lifecycle events to Kafka for downstream consumers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventPublisher {

    private final KafkaTemplate<String, TripEventDto> kafkaTemplate;

    public void publishTripRequested(TripEventDto event) {
        kafkaTemplate.send(Constants.TOPIC_TRIP_REQUESTED, event.getTripId().toString(), event);
        log.info("Published trip.requested event for trip: {}", event.getTripId());
    }

    public void publishTripAccepted(TripEventDto event) {
        kafkaTemplate.send(Constants.TOPIC_TRIP_ACCEPTED, event.getTripId().toString(), event);
        log.info("Published trip.accepted event for trip: {}", event.getTripId());
    }

    public void publishTripStarted(TripEventDto event) {
        kafkaTemplate.send(Constants.TOPIC_TRIP_STARTED, event.getTripId().toString(), event);
        log.info("Published trip.started event for trip: {}", event.getTripId());
    }

    public void publishTripCompleted(TripEventDto event) {
        kafkaTemplate.send(Constants.TOPIC_TRIP_COMPLETED, event.getTripId().toString(), event);
        log.info("Published trip.completed event for trip: {}", event.getTripId());
    }
}
