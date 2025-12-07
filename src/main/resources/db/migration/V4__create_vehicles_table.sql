CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL UNIQUE,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(50) NOT NULL,
    color VARCHAR(30) NOT NULL,
    year INTEGER NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE
);

CREATE INDEX idx_vehicle_driver_id ON vehicles(driver_id);
CREATE INDEX idx_vehicle_plate ON vehicles(plate_number);
