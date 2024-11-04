INSERT INTO theme_entity (name, background_color, widget_color, text_color)
VALUES ('Light', 0x000000, 0x000000, 0x000000),
       ('Medium', 0x000000, 0x000000, 0x000000),
       ('Dark', 0x000000, 0x000000, 0x000000),
       ('dummy', 0x000000, 0x000000, 0x000000);

INSERT INTO class_room_entity(name)
VALUES ('Q1'),
       ('3e'),
       ('7l'),
       ('dummy');

/* Max = securestPasswordProbably123!, John = 123password! and Martin = password123*/
INSERT INTO user_entity (first_name, last_name, login_name, password, system_account, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', '$2y$10$CsbEQdr99lfl9rWp18wJ3OKPINMuIuWzUgQR3Ek5F.Xj3rNQeD7KG', FALSE,
        FALSE, FALSE, 1, 0),

       ('John', 'Zimmermann', 'john.zimmermann', '$2y$10$4urnpOegHUXoQaQakLcKP.iNZxrGeaKhS.55FlAI1eJqkLeGqF.iO', FALSE,
        TRUE, TRUE, 2, 0),

       ('Martin', 'Hansen', 'martin.hansen', '$2y$10$eHQ64sFwMpF0Gz4Fc2aKVuRoND6v78AAx/Oplh.uVBYLIGATUnBQq', FALSE,
        TRUE, FALSE, 3, 0),

       ('dummy', 'dummy', 'dummy.dummy', 'password123', FALSE, TRUE, FALSE, 2, 3),
       ('gaz', 'gaz', 'gaz', 'password123', TRUE, TRUE, FALSE, 2, 3);

INSERT INTO message_entity (author_id, body, timestamp, status)
VALUES (1, 'Hey, wie geht es?', 1702672064, 0),
       (2, 'voll cool.', 1702672164, 0),
       (3, 'hals maul', 1702675064, 0),
       (4, 'dummy', 1802672064, 0);

INSERT INTO group_entity (name, two_factor_required)
VALUES ('Users', false),
       ('Moderators', true),
       ('Admin', true),
       ('Dummy', false);

INSERT INTO privilege_entity (name)
VALUES ('READ'),
       ('WRITE'),
       ('MODERATE'),
       ('DUMMY');

/* 0 = EMAIL, 1 = SMS, 2 = TOTP */
INSERT INTO two_factor_entity(method, data, secret, enabled, user_id)
VALUES (0, 'mustermann@example.com', '', true, 1),

       (0, 'mustermann@example.com', '', false, 2),
       (1, '555 5555555', '', true, 2),

       (0, 'mustermann@examle.com', '', true, 3),
       (1, '555 5555555', '', true, 3),
       (2, NULL, '', false, 3),

       (2, NULL, '', true, 4);

INSERT INTO chat_entity (time_of_creation)
VALUES (90234802),
       (92038200),
       (33400000),
       (75839435);

INSERT INTO file_entity (file_name, author_id, data_directory)
VALUES ('howtostaysingleforever.m4a', 1, 'Sexualkunde_8b'),
       ('ivo_hausaufgaben.exe', 2, 'root'),
       ('informatik_themen_fr_kühnel.docx', 3, 'Informatik_Q1'),
       ('ivo_präsentation_cover.svg', 2, 'other');

INSERT INTO illness_notification_entity (user_id, status, reason, time_stamp, expiration_time, file_entity_id)
VALUES (1, 1, 'meine kakerlake hat fieber, kann nich kommen', 293948232, 35000000, 1),
       (2, 0, 'ich mag kein erdkunde', 239482094, 35000000, 2),
       (3, 2, 'ich schwöre wenn ich jetzt keine antwort vom sekreteriat bekomm dann...', 23837348, 35000000, 3),
       (4, 0, 'ich habe 45 grad fieber', 87293933, 35000000, 4);

INSERT INTO subject_entity(name)
VALUES ('German'),
       ('Mathematics'),
       ('Informatics'),
       ('Dummy');

INSERT INTO course_entity(name, subject_id, repository_id, class_room_id)
VALUES ('Q1-German', 1, 1, 1),
       ('5e-Math', 2, 2, 1),
       ('2e-Informatics', 3, 3, 3),
       ('Dummy', 4, 4, 4);

MERGE INTO chat_entity_messages (chat_id, message_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4);

MERGE INTO chat_entity_users (chat_id, user_id)
    VALUES (1, 1),
           (1, 3),

           (2, 2),
           (2, 4),

           (3, 2),
           (3, 3),

           (4, 1),
           (4, 4);

MERGE INTO user_groups (user_id, group_id)
    VALUES (1, 1),

           (2, 1),
           (2, 2),

           (3, 1),
           (3, 2),
           (3, 3),

           (4, 4);

MERGE INTO group_privileges (group_id, privilege_id)
    VALUES (1, 1),

           (2, 1),
           (2, 2),

           (3, 1),
           (3, 2),
           (3, 3),

           (4, 4);

MERGE INTO course_users (course_id, user_id)
    VALUES (2, 2),
           (3, 1),
           (4, 4);

MERGE INTO class_room_users (class_room_id, user_id)
    VALUES (1, 1),
           (2, 2),
           (2, 3),
           (4, 4);
