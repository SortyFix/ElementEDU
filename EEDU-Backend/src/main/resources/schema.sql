-- FILES --
CREATE TABLE IF NOT EXISTS file_entity
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id      BIGINT       NULL,
    file_name      VARCHAR(255) NULL,
    data_directory VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS file_user_privileges
(
    file_id   BIGINT       NOT NULL,
    privilege VARCHAR(255) NOT NULL,
    FOREIGN KEY (file_id) REFERENCES file_entity (id)
    );


CREATE TABLE IF NOT EXISTS file_entity_tags
(
    file_entity_id BIGINT       NOT NULL,
    tags           VARCHAR(255) NULL,
    FOREIGN KEY (file_entity_id) REFERENCES file_entity (id)
);

-- Classes, Courses and Subjects --
CREATE TABLE IF NOT EXISTS class_room_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

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
    repository_id BIGINT       NOT NULL,
    class_room_id BIGINT       NULL,
    FOREIGN KEY (subject_id) REFERENCES subject_entity (id),
    FOREIGN KEY (repository_id) REFERENCES file_entity (id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id)
);

CREATE TABLE IF NOT EXISTS scheduled_appointment_entity
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id  BIGINT         NOT NULL,
    time_stamp BIGINT         NOT NULL,
    duration   BIGINT         NOT NULL,
    period     VARBINARY(255) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course_entity (id)
);

CREATE TABLE IF NOT EXISTS appointment_entry_entity
(
    id                       BIGINT PRIMARY KEY,
    time_stamp               BIGINT         NOT NULL,
    publish                  BIGINT         NOT NULL,
    duration                 BIGINT         NOT NULL,
    description              VARCHAR(255),
    homework                 VARCHAR(255),
    submit_homework          BOOLEAN        NOT NULL,
    submit_until             BIGINT         NULL,
    course_appointment_id    BIGINT         NOT NULL,
    scheduled_appointment_id BIGINT         NULL,
    FOREIGN KEY (course_appointment_id) REFERENCES course_entity (id),
    FOREIGN KEY (scheduled_appointment_id) REFERENCES scheduled_appointment_entity (id)
);

-- User --
CREATE TABLE IF NOT EXISTS theme_entity
(
    background_color_r   SMALLINT      NOT NULL,
    background_color_g   SMALLINT      NOT NULL,
    background_color_b   SMALLINT      NOT NULL,
    widget_color_r       SMALLINT    NOT NULL,
    widget_color_g       SMALLINT    NOT NULL,
    widget_color_b       SMALLINT    NOT NULL,
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS user_entity
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(255) NULL,
    last_name      VARCHAR(255) NULL,
    login_name     VARCHAR(255) NULL,
    system_account BIT          NOT NULL,
    enabled        BIT          NOT NULL,
    locked         BIT          NOT NULL,
    theme_id       BIGINT       NULL,
    status         TINYINT      NULL,
    class_room_id  BIGINT       NULL,
    FOREIGN KEY (theme_id) REFERENCES theme_entity (id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id)
);

CREATE TABLE IF NOT EXISTS credential_entity
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    enabled        BIT          NOT NULL,
    allowed_methods INT          NULL,
    data           VARCHAR(255) NULL,
    method         TINYINT      NULL,
    secret         VARCHAR(255) NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
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
    class_room_id BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    PRIMARY KEY (class_room_id, user_id),
    FOREIGN KEY (class_room_id) REFERENCES class_room_entity (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_entity
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS user_groups
(
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id),
    FOREIGN KEY (user_id) REFERENCES user_entity (id)
);

CREATE TABLE IF NOT EXISTS privilege_entity
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS group_privileges
(
    group_id     BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (group_id, privilege_id),
    FOREIGN KEY (privilege_id) REFERENCES privilege_entity (id),
    FOREIGN KEY (group_id) REFERENCES group_entity (id)
);

CREATE TABLE IF NOT EXISTS illness_notification_entity
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    status          TINYINT      NOT NULL,
    reason          VARCHAR(255) NOT NULL,
    time_stamp      BIGINT       NOT NULL,
    expiration_time BIGINT       NOT NULL,
    file_entity_id  BIGINT       NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user_entity (id),
    FOREIGN KEY (file_entity_id) REFERENCES file_entity (id)
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

CREATE TABLE IF NOT EXISTS post_entity
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    thumbnailurl VARCHAR(255) NOT NULL,
    body VARCHAR(255) NOT NULL,
    time_of_creation BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS post_user_read_privileges
(
    post_id BIGINT NOT NULL,
    read_privileges VARCHAR(255) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_user_edit_privileges
(
    post_id BIGINT NOT NULL,
    edit_privileges VARCHAR(255) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_tags
(
    post_id BIGINT NOT NULL,
    tags VARCHAR(255) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post_entity (id) ON DELETE CASCADE
);

INSERT INTO theme_entity (name, background_color_r, background_color_g, background_color_b, widget_color_r, widget_color_g, widget_color_b)
VALUES ('hehe', 30, 30, 30, 50, 50, 50);


INSERT INTO user_entity (first_name, last_name, login_name, system_account, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', FALSE, TRUE, FALSE, 1, 0),    -- PRESENT
       ('John', 'Zimmermann', 'john.zimmermann', FALSE, TRUE, FALSE, 1, 1),  -- EXCUSED
       ('Martin', 'Hansen', 'martin.hansen', FALSE, TRUE, FALSE, 1, 2),      -- UNEXCUSED
       ('Lora', 'Schmidt', 'lora.schmidt', FALSE, TRUE, FALSE, 1, 1),        -- EXCUSED
       ('Sara', 'Müller', 'sara.mueller', TRUE, TRUE, FALSE, 1, 0),          -- PRESENT
       ('Tom', 'Bauer', 'tom.bauer', TRUE, FALSE, FALSE, 1, 2),              -- UNEXCUSED
       ('Lisa', 'Klein', 'lisa.klein', TRUE, TRUE, FALSE, 1, 3),             -- PROSPECTIVE
       ('Oliver', 'Wagner', 'oliver.wagner', FALSE, TRUE, TRUE, 1, 0),       -- PRESENT
       ('Sophia', 'Becker', 'sophia.becker', FALSE, FALSE, FALSE, 1, 1),     -- EXCUSED
       ('Liam', 'Schneider', 'liam.schneider', FALSE, TRUE, TRUE, 1, 3),     -- PROSPECTIVE
       ('Emma', 'Fischer', 'emma.fischer', TRUE, TRUE, FALSE, 1, 2),         -- UNEXCUSED
       ('Noah', 'Weber', 'noah.weber', FALSE, TRUE, FALSE, 1, 1),            -- EXCUSED
       ('Mia', 'Hoffmann', 'mia.hoffmann', TRUE, TRUE, FALSE, 1, 0),         -- PRESENT
       ('Ethan', 'Schwarz', 'ethan.schwarz', TRUE, TRUE, FALSE, 1, 3),       -- PROSPECTIVE
       ('Charlotte', 'Zimmer', 'charlotte.zimmer', FALSE, TRUE, TRUE, 1, 2), -- UNEXCUSED
       ('James', 'Krause', 'james.krause', FALSE, TRUE, FALSE, 1, 1); -- EXCUSED

