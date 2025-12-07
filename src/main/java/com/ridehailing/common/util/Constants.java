package com.ridehailing.common.util;

public class Constants {
    
    // Kafka Topics
    public static final String TOPIC_TRIP_REQUESTED = "trip.requested";
    public static final String TOPIC_TRIP_ACCEPTED = "trip.accepted";
    public static final String TOPIC_TRIP_STARTED = "trip.started";
    public static final String TOPIC_TRIP_COMPLETED = "trip.completed";
    
    // Redis Keys
    public static final String REDIS_DRIVER_LOCATION_PREFIX = "driver:location:";
    
    // Security
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    private Constants() {
        // Utility class
    }
}
