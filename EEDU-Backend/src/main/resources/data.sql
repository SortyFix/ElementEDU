/*
 * This file is for testing purposes only.
 * It provides test users and inserts them into the database
 */
INSERT INTO user_entity (first_name, last_name, login_name, email, password, enabled, locked) VALUES
('Max', 'Mustermann', 'max.mustermann', 'max.musterman@example.com', 'password123', TRUE, FALSE),
('John', 'Zimmermann', 'john.zimmermann', 'john.zimmerman@example.com', 'password123', TRUE, TRUE),
('Martin', 'Hansen', 'martin.hansen', 'martin.hansen@example.com', 'password123', FALSE, FALSE);