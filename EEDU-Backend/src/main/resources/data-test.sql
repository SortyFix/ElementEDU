INSERT INTO theme_entity (name, background_color_r, background_color_g, background_color_b, widget_color_r, widget_color_g, widget_color_b)
VALUES ('Light', 0, 0, 0, 0, 0, 0),
       ('Medium', 0, 0, 0, 0, 0, 0),
       ('Dark', 0, 0, 0, 0, 0,0),
       ('dummy', 0, 0,0, 0, 0, 0);

INSERT INTO class_room_entity(name)
VALUES ('Q1'),
       ('3e'),
       ('7l'),
       ('dummy');

/* Max = securestPasswordProbably123!, John = 123password! and Martin = password123*/
INSERT INTO user_entity (first_name, last_name, login_name, account_type, system_account, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', 0, FALSE, FALSE, FALSE, 1, 0),
       ('John', 'Zimmermann', 'john.zimmermann', 1, FALSE, TRUE, TRUE, 2, 0),
       ('Martin', 'Hansen', 'martin.hansen', 1, FALSE, TRUE, FALSE, 3, 0),
       ('dummy', 'dummy', 'dummy.dummy', 0, FALSE, TRUE, FALSE, 2, 3),
       ('gaz', 'gaz', 'gaz', 2, TRUE, TRUE, FALSE, 2, 3);

INSERT INTO message_entity (author_id, body, timestamp, status)
VALUES (1, 'Hey, wie geht es?', 1702672064, 0),
       (2, 'voll cool.', 1702672164, 0),
       (3, 'hals maul', 1702675064, 0),
       (4, 'dummy', 1802672064, 0);

INSERT INTO group_entity (id) VALUES ('user'),('moderator'),('admin'),('dummy');

INSERT INTO privilege_entity (id) VALUES ('READ'), ('WRITE'), ('MODERATE'), ('DUMMY');

/* 0 = PASSWORD, 1 = EMAIL, 2 = SMS, 3 = TOTP */
INSERT INTO credential_entity(method, data, secret, enabled, user_id)
VALUES (0, '$2y$10$CsbEQdr99lfl9rWp18wJ3OKPINMuIuWzUgQR3Ek5F.Xj3rNQeD7KG', '', true, 1),
       (1, 'mustermann@example.com', '', true, 1),

       (0, '$2y$10$4urnpOegHUXoQaQakLcKP.iNZxrGeaKhS.55FlAI1eJqkLeGqF.iO', '', true, 2),
       (1, 'mustermann@example.com', '', false, 2),
       (2, '555 5555555', '', true, 2),

       (0, '$2y$10$eHQ64sFwMpF0Gz4Fc2aKVuRoND6v78AAx/Oplh.uVBYLIGATUnBQq', '', true, 3),
       (1, 'mustermann@examle.com', '', true, 3),
       (2, '555 5555555', '', true, 3),
       (3, NULL, '', false, 3),

       (3, NULL, '', true, 4);

INSERT INTO file_entity (data_directory)
VALUES ('Sexualkunde_8b'),
       ('root'),
       ('Informatik_Q1'),
       ('other');

--- Courses
INSERT INTO subject_entity(name)
VALUES ('German'),
       ('Mathematics'),
       ('Informatics'),
       ('Dummy');

INSERT INTO course_entity(name, subject_id, repository_id, class_room_id)
VALUES ('Q1-German', 'German', 1, 1),
       ('5e-Math', 'Mathematics', 2, 1),
       ('2e-Informatics', 'Informatics', 3, 3),
       ('Dummy', 'Dummy', 4, 4);

INSERT INTO chat_entity (time_of_creation)
VALUES (90234802),
       (92038200),
       (33400000),
       (75839435);

INSERT INTO illness_notification_entity (user_id, status, reason, time_stamp, expiration_time, file_entity_id)
VALUES (1, 1, 'meine kakerlake hat fieber, kann nich kommen', 293948232, 35000000, 1),
       (2, 0, 'ich mag kein erdkunde', 239482094, 35000000, 2),
       (3, 2, 'ich schwöre wenn ich jetzt keine antwort vom sekreteriat bekomm dann...', 23837348, 35000000, 3),
       (4, 0, 'ich habe 45 grad fieber', 87293933, 35000000, 4);

MERGE INTO chat_entity_messages (chat_id, message_id) VALUES (1, 1), (2, 2), (3, 3), (4, 4);

MERGE INTO chat_entity_users (chat_id, user_id) VALUES (1, 1), (1, 3), (2, 2), (2, 4), (3, 2), (3, 3), (4, 1), (4, 4);

MERGE INTO user_groups (user_id, group_id) VALUES (1, 'user'), (2, 'user'), (2, 'moderator'), (3, 'user'), (3, 'moderator'), (3, 'admin'), (4, 'dummy');

MERGE INTO group_privileges (group_id, privilege_id)
VALUES ('user', 'READ'),
       ('moderator', 'READ'),
       ('moderator', 'WRITE'),
       ('admin', 'READ'),
       ('admin', 'WRITE'),
       ('admin', 'MODERATE'),
       ('dummy', 'DUMMY');

MERGE INTO course_users (course_id, user_id) VALUES (2, 2), (3, 1), (4, 4);

MERGE INTO class_room_users (class_room_id, user_id) VALUES (1, 1), (2, 2), (2, 3), (4, 4);
