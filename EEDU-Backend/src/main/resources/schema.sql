-- This table stores information about different themes
CREATE TABLE IF NOT EXISTS theme_entity
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255),
    background_color INT,
    widget_color     INT,
    text_color       INT
);

-- This table represents the user entity.
-- It contains information about the user's id, first name, last name, login name, email, password status (if they're enabled or locked).
CREATE TABLE IF NOT EXISTS user_entity
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    login_name VARCHAR(255),
    password   VARCHAR(255),
    enabled    BOOLEAN NOT NULL DEFAULT false,
    locked     BOOLEAN NOT NULL DEFAULT false,
    theme_id   BIGINT REFERENCES theme_entity (id),
    status ENUM('PRESENT', 'EXCUSED', 'UNEXCUSED', 'PROSPECTIVE')
);



-- This table stores information about different groups
CREATE TABLE IF NOT EXISTS group_entity
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255),
    two_factor_required BOOLEAN
);

-- This table is a relational table that connects users and their associated groups
CREATE TABLE IF NOT EXISTS privilege_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);


-- This table is a for two factor instances connecting users and their security
CREATE TABLE IF NOT EXISTS two_factor_entity
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    method  ENUM('EMAIL', 'SMS', 'TOTP') NOT NULL,
    data    VARCHAR(255) NULL,
    secret  VARCHAR(255) NOT NULL,
    enabled BOOLEAN      NOT NULL,
    user_id BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

-- This table is a relational table that connects groups and their associated privileges
CREATE TABLE IF NOT EXISTS user_groups
(
    user_id  BIGINT,
    group_id BIGINT,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES group_entity (id) ON DELETE CASCADE
);

-- This table stores information about different privileges.
CREATE TABLE IF NOT EXISTS group_privileges
(
    group_id     BIGINT,
    privilege_id BIGINT,
    PRIMARY KEY (group_id, privilege_id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privilege_entity (id) ON DELETE CASCADE
);

-- This table stores information about different uploaded files.
CREATE TABLE IF NOT EXISTS file_entity
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255),
    author_id BIGINT,
    file_path VARCHAR(255)
);

-- This table stores which users (by Set<UserEntity>) should have permission to access a certain file.
CREATE TABLE IF NOT EXISTS file_user_permissions
(
    file_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (file_id, user_id),
    FOREIGN KEY (file_id) REFERENCES file_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE
);

-- This table stores which groups (by Set<GroupEntity>) should have permission to access a certain file.
CREATE TABLE IF NOT EXISTS file_group_permissions
(
    file_id  BIGINT,
    group_id BIGINT,
    PRIMARY KEY (file_id, group_id),
    FOREIGN KEY (file_id) REFERENCES file_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES group_entity (id) ON DELETE CASCADE
);

-- This table stores which tags should be given to a file. Multiple tags can be given, hence this table.
CREATE TABLE IF NOT EXISTS file_tags
(
    file_id BIGINT,
    tags    VARCHAR(255),
    FOREIGN KEY (file_id) REFERENCES file_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS illness_notification_entity
(
    notification_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    status            VARCHAR(255),
    notification_date DATE
);
