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

-- Insert more posts into the `post` table
INSERT INTO post_entity (id, author, title, thumbnailURL, body, time_of_creation)
VALUES
    (3, 'Alice Cooper', 'Understanding Java Streams', null, 'Dive deep into Java Streams API.', 1698854400000),
    (4, 'Bob Marley', 'Kotlin for Beginners', null, 'An introduction to Kotlin programming language.', 1698940800000),
    (5, 'Eve Adams', 'Effective Unit Testing', null, 'How to write effective unit tests in Java.', 1699027200000),
    (6, 'Charlie Daniels', 'REST APIs with Spring Boot', null, 'Guide to creating REST APIs with Spring Boot.', 1699113600000),
    (7, 'Dana White', 'Microservices Architecture', null, 'Learn how to design microservices.', 1699200000000),
    (8, 'Ernest Hemingway', 'Deploying Apps with Docker', null, 'An introduction to Docker and app deployment.', 1699286400000),
    (9, 'Grace Hopper', 'Demystifying Kubernetes', null, 'Kubernetes for managing containerized apps.', 1699372800000),
    (10, 'Ada Lovelace', 'AI for Beginners', null, 'Understanding the basics of Artificial Intelligence.', 1699459200000),
    (11, 'Alan Turing', 'Intro to Cryptography', null, 'Learn about cryptography and securing information.', 1699545600000),
    (12, 'Linus Torvalds', 'Version Control with Git', null, 'Master Git for version control.', 1699632000000),
    (13, 'Margaret Hamilton', 'Building Robust Systems', null, 'How to build fault-tolerant and robust systems.', 1699718400000),
    (14, 'Tim Berners-Lee', 'Web Development Basics', null, 'Basics of developing web applications.', 1699804800000),
    (15, 'Yonas Nieder', 'The wonders of markdown with Yonas Nieder Fernández', null,
     '# Markdown syntax guide

## Headers

# This is a Heading h1
## This is a Heading h2
###### This is a Heading h6

## Emphasis

*This text will be italic*
_This will also be italic_

**This text will be bold**
__This will also be bold__

_You **can** combine them_

## Lists

### Unordered

* Item 1
* Item 2
* Item 2a
* Item 2b
    * Item 3a
    * Item 3b

### Ordered

1. Item 1
2. Item 2
3. Item 3
    1. Item 3a
    2. Item 3b

## Images

![This is an alt text.](/image/sample.webp "This is a sample image.")

## Links

You may be using [Markdown Live Preview](https://markdownlivepreview.com/).

## Blockquotes

> Markdown is a lightweight markup language with plain-text-formatting syntax, created in 2004 by John Gruber with Aaron Swartz.
>
>> Markdown is often used to format readme files, for writing messages in online discussion forums, and to create rich text using a plain text editor.

## Tables

| Left columns  | Right columns |
| ------------- |:-------------:|
| left foo      | right foo     |
| left bar      | right bar     |
| left baz      | right baz     |

## Blocks of code

```
let message = ''Hello world'';
alert(message);
```

## Inline code

This web site is using `markedjs/marked`.',
     1699891200000);

-- Insert more into the `post_user_edit_privileges` table
INSERT INTO post_user_edit_privileges (post_id, edit_privileges)
VALUES
    (3, 'ADMIN'),
    (4, 'ADMIN'),
    (5, 'ADMIN'),
    (6, 'ADMIN'),
    (7, 'ADMIN'),
    (8, 'ADMIN'),
    (9, 'ADMIN'),
    (10, 'ADMIN'),
    (11, 'ADMIN'),
    (12, 'ADMIN'),
    (13, 'ADMIN'),
    (14, 'ADMIN'),
    (15, 'ADMIN');

-- Insert more into the `post_tags` table
INSERT INTO post_tags (post_id, tags)
VALUES
    (3, 'java'),
    (4, 'kotlin'),
    (5, 'testing'),
    (6, 'springboot'),
    (7, 'microservices'),
    (8, 'docker'),
    (9, 'kubernetes'),
    (10, 'ai'),
    (11, 'cryptography'),
    (12, 'git'),
    (13, 'systems'),
    (14, 'webdev'),
    (15, 'markdown'),
    (15, 'yonas'),
    (15, 'html');
