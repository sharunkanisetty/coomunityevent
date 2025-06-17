-- Drop the existing table
DROP TABLE IF EXISTS carbon_footprints;

-- Recreate the table with the correct constraints
CREATE TABLE carbon_footprints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    calculation_date DATETIME NOT NULL,
    distance_traveled DOUBLE,
    energy_consumption DOUBLE,
    event_id BIGINT,
    measurement_period VARCHAR(20) NOT NULL,
    total_carbon_offset DOUBLE,
    transportation_emissions DOUBLE,
    transportation_type VARCHAR(50),
    user_id BIGINT NOT NULL,
    waste_generated DOUBLE,
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Make event_id nullable
ALTER TABLE carbon_footprints MODIFY COLUMN event_id BIGINT NULL;

-- Add measurement_period column with default value
ALTER TABLE carbon_footprints ADD COLUMN measurement_period VARCHAR(10) NOT NULL DEFAULT 'DAILY'; 