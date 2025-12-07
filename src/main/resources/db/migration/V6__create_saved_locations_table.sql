CREATE TABLE saved_locations (
    id BIGSERIAL PRIMARY KEY,
    rider_id BIGINT NOT NULL,
    label VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(255),
    FOREIGN KEY (rider_id) REFERENCES riders(id) ON DELETE CASCADE
);

CREATE INDEX idx_saved_location_rider_id ON saved_locations(rider_id);
