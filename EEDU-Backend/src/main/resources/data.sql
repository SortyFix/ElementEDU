/*
 * This file is for testing purposes only.
 * It provides test users and inserts them into the database
 */
INSERT INTO users (first_name, last_name, email, password, enabled, locked) VALUES
('Max', 'Mustermann', 'max.musterman@example.com', 'password123', TRUE, FALSE),
('John', 'Zimmermann', 'john.zimmerman@example.com', 'password123', TRUE, TRUE),
('Martin', 'Hansen', 'martin.hansen@example.com', 'password123', FALSE, FALSE);