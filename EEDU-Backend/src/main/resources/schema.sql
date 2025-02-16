-- FILES --
CREATE TABLE IF NOT EXISTS file_entity
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS room_entity
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS frequent_appointment_entity
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id        BIGINT NOT NULL,
    room_id          BIGINT NOT NULL,
    start_time_stamp BIGINT NOT NULL,
    until_time_stamp   BIGINT NOT NULL,
    duration         BIGINT NOT NULL,
    frequency           INT    NOT NULL,
    FOREIGN KEY (course_id) REFERENCES course_entity (id),
    FOREIGN KEY (room_id) REFERENCES room_entity(id)
);

CREATE TABLE IF NOT EXISTS appointment_entry_entity
(
    id                       BIGINT PRIMARY KEY,
    duration                 BIGINT         NOT NULL,
    publish                  BIGINT         NOT NULL,
    description              VARCHAR(1000)   NULL,
    assignment_description   VARCHAR(255)   NULL,
    publish_assignment       BIGINT         NULL,
    submit_assignment_until  BIGINT         NULL,
    room_id                  BIGINT         NULL,
    course_appointment_id    BIGINT         NOT NULL,
    frequent_appointment_id BIGINT         NULL,
    FOREIGN KEY (room_id) REFERENCES room_entity (id),
    FOREIGN KEY (course_appointment_id) REFERENCES course_entity (id),
    FOREIGN KEY (frequent_appointment_id) REFERENCES frequent_appointment_entity (id)
);

-- User --
CREATE TABLE IF NOT EXISTS theme_entity
(
    background_color_r   TINYINT     NOT NULL,
    background_color_g   TINYINT     NOT NULL,
    background_color_b   TINYINT     NOT NULL,
    widget_color_r       TINYINT     NOT NULL,
    widget_color_g       TINYINT     NOT NULL,
    widget_color_b       TINYINT     NOT NULL,
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
    account_type   TINYINT      NOT NULL,
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
    body       VARCHAR(1500) NOT NULL,
    timestamp  BIGINT       NOT NULL,
    status     TINYINT,
    FOREIGN KEY (author_id) REFERENCES user_entity (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_entity
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    thumbnailurl VARCHAR(255),
    body VARCHAR(65000) NOT NULL,
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


INSERT INTO class_room_entity (name)
VALUES ('Room 101'),
       ('Room 102'),
       ('Room 103');

INSERT INTO subject_entity (name)
VALUES ('Mathematics'),
       ('Physics'),
       ('History'),
       ('Computer Science');

INSERT INTO group_entity (name) VALUES ('parent'), ('girl'), ('boys');

INSERT INTO theme_entity (name, background_color_r, background_color_g, background_color_b, widget_color_r, widget_color_g, widget_color_b)
VALUES ('dark', -120, -120, -100, -105, -105, -90);

INSERT INTO user_entity (first_name, last_name, login_name, account_type, system_account, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', 2, FALSE, TRUE, FALSE, 1, 0),    -- PRESENT Student
       ('John', 'Zimmermann', 'john.zimmermann', 2, FALSE, TRUE, FALSE, 1, 1),  -- EXCUSED Student
       ('Martin', 'Hansen', 'martin.hansen', 2, FALSE, TRUE, FALSE, 1, 2),      -- UNEXCUSED Student
       ('Lora', 'Schmidt', 'lora.schmidt', 1, FALSE, TRUE, FALSE, 1, 1),        -- EXCUSED Teacher
       ('Sara', 'Müller', 'sara.mueller', 1, TRUE, TRUE, FALSE, 1, 0),          -- PRESENT Teacher
       ('Tom', 'Bauer', 'tom.bauer', 1, TRUE, FALSE, FALSE, 1, 2),              -- UNEXCUSED Teacher
       ('Lisa', 'Klein', 'lisa.klein', 2, TRUE, TRUE, FALSE, 1, 3),             -- PROSPECTIVE Student
       ('Oliver', 'Wagner', 'oliver.wagner', 2, FALSE, TRUE, TRUE, 1, 0),       -- PRESENT Student
       ('Sophia', 'Becker', 'sophia.becker', 2, FALSE, FALSE, FALSE, 1, 1),     -- EXCUSED Student
       ('Liam', 'Schneider', 'liam.schneider', 2, FALSE, TRUE, TRUE, 1, 3),     -- PROSPECTIVE Student
       ('Emma', 'Fischer', 'emma.fischer', 2, TRUE, TRUE, FALSE, 1, 2),         -- UNEXCUSED Student
       ('Noah', 'Weber', 'noah.weber', 2, FALSE, TRUE, FALSE, 1, 1),            -- EXCUSED Student
       ('Mia', 'Hoffmann', 'mia.hoffmann', 2, TRUE, TRUE, FALSE, 1, 0),         -- PRESENT Student
       ('Ethan', 'Schwarz', 'ethan.schwarz', 2, TRUE, TRUE, FALSE, 1, 3),       -- PROSPECTIVE Student
       ('Charlotte', 'Zimmer', 'charlotte.zimmer', 2, FALSE, TRUE, TRUE, 1, 2), -- UNEXCUSED Student
       ('James', 'Krause', 'james.krause', 2, FALSE, TRUE, FALSE, 1, 1);        -- EXCUSED Student

INSERT INTO file_entity (data_directory)
VALUES ('/repo/algebra/101'),   -- File for Algebra 101
       ('/repo/calculus/101'), -- File for Calculus 101
       ('/repo/physics/101'),   -- File for Physics 101
       ('/repo/history/101'),   -- File for History 101
       ('/repo/programming'); -- File for Introduction to Programming

INSERT INTO course_entity (name, subject_id, repository_id, class_room_id)
VALUES ('Algebra 101', 1, 1, 1),  -- Mathematics (Algebra), Room 101
       ('Calculus 101', 1, 2, 1), -- Mathematics (Calculus), Room 101
       ('Physics 101', 2, 3, 2),  -- Physics, Room 102
       ('History 101', 3, 4, 3),  -- History, Room 103
       ('Introduction to Programming', 4, 5, 2); -- Computer Science, Room 102

INSERT INTO room_entity (name) VALUES ('403'), ('504'), ('204');

INSERT INTO course_users (course_id, user_id)
VALUES (1, 5), -- Sara Müller to Algebra 101
       (1, 6), -- Tom Bauer to Algebra 101
       (2, 5), -- Sara Müller to Calculus 101
       (3, 7), -- Lisa Klein to Physics 101
       (4, 5), -- Sara Müller to History 101
       (5, 5), -- Sara Müller to Introduction to Programming
       (1, 1), -- Max Mustermann to Algebra 101
       (1, 2), -- John Zimmermann to Algebra 101
       (1, 3), -- Martin Hansen to Algebra 101
       (2, 1), -- Max Mustermann to Calculus 101
       (2, 7), -- Lisa Klein to Calculus 101
       (3, 1), -- Max Mustermann to Physics 101
       (3, 4), -- Lora Schmidt to Physics 101
       (4, 3), -- Martin Hansen to History 101
       (4, 4), -- Lora Schmidt to History 101
       (5, 2), -- John Zimmermann to Introduction to Programming
       (5, 10); -- Liam Schneider to Introduction to Programming