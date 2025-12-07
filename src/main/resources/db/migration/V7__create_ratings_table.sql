CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL UNIQUE,
    rider_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    rider_rating DECIMAL(2, 1),
    driver_rating DECIMAL(2, 1),
    rider_comment TEXT,
    driver_comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (rider_id) REFERENCES riders(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

CREATE INDEX idx_rating_trip_id ON ratings(trip_id);
CREATE INDEX idx_rating_rider_id ON ratings(rider_id);
CREATE INDEX idx_rating_driver_id ON ratings(driver_id);
