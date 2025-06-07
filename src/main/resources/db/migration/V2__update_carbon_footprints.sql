-- Make event_id nullable
ALTER TABLE carbon_footprints MODIFY COLUMN event_id BIGINT NULL;

-- Add measurement_period column with default value
ALTER TABLE carbon_footprints ADD COLUMN measurement_period VARCHAR(10) NOT NULL DEFAULT 'DAILY'; 