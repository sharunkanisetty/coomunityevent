-- Insert sample users
INSERT IGNORE INTO users (email, username, password, full_name, role)
VALUES
    ('admin@example.com', 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'Admin User', 'ADMIN'),
    ('user@example.com', 'user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'Regular User', 'USER');

-- Insert sample events
INSERT IGNORE INTO events (title, description, date, location, event_type, organizer_id)
VALUES
    ('Community Cleanup', 'Join us for a community cleanup event at the local park.', DATE_ADD(NOW(), INTERVAL 7 DAY), 'Central Park', 'CLEANUP', 1),
    ('Tree Planting', 'Help us plant trees in the neighborhood.', DATE_ADD(NOW(), INTERVAL 14 DAY), 'Community Garden', 'PLANTING', 1),
    ('Environmental Workshop', 'Learn about sustainable living practices.', DATE_ADD(NOW(), INTERVAL 21 DAY), 'Community Center', 'WORKSHOP', 2);

-- Insert sample reward worth 50 points
INSERT IGNORE INTO rewards (reward_name, description, points_cost, is_active, stock_quantity, image_url, reward_type)
VALUES ('Eco-Friendly Tote Bag', 'A reusable tote bag for your groceries.', 50, true, 10, 'https://example.com/tote.jpg', 'MERCHANDISE');

ALTER TABLE carbon_footprints MODIFY COLUMN event_id BIGINT NULL;