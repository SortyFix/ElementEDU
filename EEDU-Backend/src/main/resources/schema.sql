-- This table represents the user entity.
-- It contains information about the user's id, first name, last name, login name, email, password status (if they're enabled or locked).
CREATE TABLE user_entity
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    login_name    VARCHAR(255),
    password    VARCHAR(255),
    enabled     BOOLEAN,
    locked      BOOLEAN
);

-- This table stores information about different groups
CREATE TABLE group_entity
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- This table is a relational table that connects users and their associated groups
CREATE TABLE privilege_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- This table is a relational table that connects groups and their associated privileges
CREATE TABLE user_groups
(
    user_id  BIGINT,
    group_id BIGINT,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES group_entity (id) ON DELETE CASCADE
);

-- This table stores information about different privileges.
CREATE TABLE group_privileges
(
    group_id     BIGINT,
    privilege_id BIGINT,
    PRIMARY KEY (group_id, privilege_id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privilege_entity (id) ON DELETE CASCADE
);
