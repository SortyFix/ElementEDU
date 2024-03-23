-- The 'file_entity' table is responsible for storing all information related to files. This includes the author's id, the file's unique id, the file's name and its physical path in storage.
CREATE TABLE IF NOT EXISTS theme_entity
(
    background_color INT          NOT NULL,
    text_color       INT          NOT NULL,
    widget_color     INT          NOT NULL,
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS class_room_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- The 'file_entity_tags' table is used to keep track of tags applied to file entities in the 'file_entity' table.
CREATE TABLE IF NOT EXISTS user_entity
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(255) NULL,
    last_name      VARCHAR(255) NULL,
    login_name     VARCHAR(255) NULL,
    password       VARCHAR(255) NULL,
    system_account BIT      NOT NULL,
    enabled        BIT          NOT NULL,
    locked         BIT          NOT NULL,
    theme_id       BIGINT       NULL,
    status         TINYINT      NULL,
    class_room_id  BIGINT       NULL,
    FOREIGN KEY (theme_id) REFERENCES theme_entity (id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id)
);

-- The 'group_privileges' table is an associative (junction) table that links groups to their privileges.
CREATE TABLE IF NOT EXISTS group_entity
(
    two_factor_required BIT          NOT NULL,
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NULL
);

-- The 'file_user_permissions' table is used similarly to 'file_group_permissions', but is tailored towards individual users. It defines which users have explicit access to which files.
CREATE TABLE IF NOT EXISTS privilege_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL
);

-- The 'group_entity' table contains information about user groups. Each group can have a unique name and optionally require two-factor authentication.
CREATE TABLE IF NOT EXISTS two_factor_entity
(
    enabled BIT          NOT NULL,
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT       NOT NULL,
    data    VARCHAR(255) NULL,
    method  TINYINT      NULL,
    secret  VARCHAR(255) NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

-- The 'illness_notification_entity' table keeps a record of all illness notifications sent by users. This includes a unique notification id, the user id of the sender, the reason for the notification and its status.
CREATE TABLE IF NOT EXISTS file_entity
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id       BIGINT       NULL,
    file_name       VARCHAR(255) NULL,
    data_directory  VARCHAR(255) NOT NULL
);

-- The 'two_factor_entity' table keeps track of users' two-factor authentication settings.
CREATE TABLE IF NOT EXISTS file_user_privileges
(
    file_id     BIGINT NOT NULL,
    privilege   VARCHAR(255) NOT NULL,
    FOREIGN KEY (file_id) REFERENCES file_entity (id)
);

-- The 'privilege_entity' table stores a set of privileges that can be assigned to a group in the 'group_entity' table.
CREATE TABLE IF NOT EXISTS file_entity_tags
(
    file_entity_id BIGINT       NOT NULL,
    tags           VARCHAR(255) NULL,
    FOREIGN KEY (file_entity_id) REFERENCES file_entity (id)
);


CREATE TABLE IF NOT EXISTS illness_notification_entity
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    status          TINYINT NOT NULL,
    reason          VARCHAR(255) NOT NULL,
    time_stamp      BIGINT NOT NULL,
    expiration_time BIGINT NOT NULL,
    file_entity_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user_entity (id),
    FOREIGN KEY (file_entity_id) REFERENCES file_entity (id)
);

-- The 'theme_entity' table contains various themes that can be applied to the user interface. Each theme includes specific colors for different elements of the interface.
CREATE TABLE IF NOT EXISTS user_groups
(
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

-- The 'user_entity' table contains all information relevant to each individual user. This includes a unique user id, the theme they're using, their first and last names, login name, password, as well as the status and account settings.
CREATE TABLE IF NOT EXISTS group_privileges
(
    group_id     BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (group_id, privilege_id),
    FOREIGN KEY (privilege_id) REFERENCES privilege_entity (id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id)
);

CREATE TABLE IF NOT EXISTS chat_entity
(
    chat_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    time_of_creation BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_entity_users
(
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (chat_id, user_id),
    FOREIGN KEY (chat_id) REFERENCES chat_entity (chat_id)
);

CREATE TABLE IF NOT EXISTS chat_entity_messages
(
    chat_id    BIGINT NOT NULL,
    message_id BIGINT NOT NULL,
    PRIMARY KEY (chat_id, message_id),
    FOREIGN KEY (chat_id) REFERENCES chat_entity (chat_id)
);

CREATE TABLE IF NOT EXISTS message_entity
(
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id  BIGINT       NOT NULL,
    body       VARCHAR(255) NOT NULL,
    timestamp  BIGINT       NOT NULL,
    status     TINYINT,
    FOREIGN KEY (author_id) REFERENCES user_entity (id) ON DELETE CASCADE
);

-- Courses

CREATE TABLE IF NOT EXISTS subject_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS course_entity
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    subject_id    BIGINT       NOT NULL,
    class_room_id BIGINT       NULL,
    FOREIGN KEY (subject_id) REFERENCES subject_entity (id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id)
);

CREATE TABLE IF NOT EXISTS course_appointment
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_day  TINYINT NOT NULL,
    start     BIGINT  NOT NULL,
    course_id BIGINT  NOT NULL,
    duration  NUMERIC NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course_entity (id)
);

CREATE TABLE IF NOT EXISTS course_users
(
    course_id BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    PRIMARY KEY (course_id, user_id),
    FOREIGN KEY (course_id) REFERENCES course_entity (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

CREATE TABLE IF NOT EXISTS class_room_users
(
    class_room_id  BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (class_room_id, user_id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE
)
