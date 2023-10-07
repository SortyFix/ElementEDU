/*
 * This file is for testing purposes only.
 * It automatically creates a table with the columns
 * id, first_name, last_name, email, password, enabled and locked
 * These are required to test the userEntity system
 */
CREATE TABLE users
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    email       VARCHAR(255),
    password    VARCHAR(255),
    enabled     BOOLEAN,
    locked      BOOLEAN
);