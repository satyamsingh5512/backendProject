CREATE TABLE driver_earnings (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL UNIQUE,
    gross_amount DECIMAL(10, 2) NOT NULL,
    commission_rate DECIMAL(5, 2) NOT NULL DEFAULT 20.00,
    commission_amount DECIMAL(10, 2) NOT NULL,
    net_amount DECIMAL(10, 2) NOT NULL,
    payout_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payout_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES drivers(id),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

CREATE INDEX idx_earnings_driver_id ON driver_earnings(driver_id);
CREATE INDEX idx_earnings_trip_id ON driver_earnings(trip_id);
CREATE INDEX idx_earnings_payout_status ON driver_earnings(payout_status);
CREATE INDEX idx_earnings_created_at ON driver_earnings(created_at);
