CREATE TABLE trips (
    id BIGSERIAL PRIMARY KEY,
    rider_id BIGINT NOT NULL,
    driver_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    origin_latitude DOUBLE PRECISION NOT NULL,
    origin_longitude DOUBLE PRECISION NOT NULL,
    destination_latitude DOUBLE PRECISION NOT NULL,
    destination_longitude DOUBLE PRECISION NOT NULL,
    estimated_fare DECIMAL(10, 2),
    final_fare DECIMAL(10, 2),
    distance_km DECIMAL(10, 2),
    surge_multiplier DECIMAL(3, 2) DEFAULT 1.00,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    FOREIGN KEY (rider_id) REFERENCES riders(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

CREATE INDEX idx_trip_rider_id ON trips(rider_id);
CREATE INDEX idx_trip_driver_id ON trips(driver_id);
CREATE INDEX idx_trip_status ON trips(status);
CREATE INDEX idx_trip_requested_at ON trips(requested_at);
