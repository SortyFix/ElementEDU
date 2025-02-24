INSERT INTO theme_entity (name, background_color_r, background_color_g, background_color_b, widget_color_r,
                          widget_color_g, widget_color_b)
VALUES ('Light', 0, 0, 0, 0, 0, 0),
       ('Medium', 0, 0, 0, 0, 0, 0),
       ('Dark', 0, 0, 0, 0, 0, 0),
       ('dummy', 0, 0, 0, 0, 0, 0);

INSERT INTO subject_entity(id)
VALUES ('subject0'),
       ('subject1'),
       ('subject2'),
       ('subject3'),
       ('subject4'),
       ('subject5'),
       ('subject6'),
       ('subject7'),
       ('subject8'),
       ('subject9')
;

INSERT INTO room_entity(id)
VALUES ('room0'),
       ('room1'),
       ('room2'),
       ('room3'),
       ('room4'),
       ('room5'),
       ('room6'),
       ('room7'),
       ('room8'),
       ('room9')
;

INSERT INTO class_room_entity(id)
VALUES ('classroom0'),
       ('classroom1'),
       ('classroom2'),
       ('classroom3'),
       ('classroom4'),
       ('classroom5'),
       ('classroom6'),
       ('classroom7'),
       ('classroom8'),
       ('classroom9')
;

INSERT INTO file_entity (data_directory)
VALUES ('repository0'),
       ('repository1'),
       ('repository2'),
       ('repository3'),
       ('repository4'),
       ('repository5'),
       ('repository6'),
       ('repository7'),
       ('repository8'),
       ('repository9');

INSERT INTO course_entity(name, subject_id, repository_id, class_room_id)
VALUES ('course0', 'subject0', 1, 'classroom0'),
       ('course1', 'subject1', 2, 'classroom1'),
       ('course2', 'subject2', 3, 'classroom2'),
       ('course3', 'subject3', 4, 'classroom3'),
       ('course4', 'subject4', 5, 'classroom4'),
       ('course5', 'subject5', 6, 'classroom5'),
       ('course6', 'subject6', 7, 'classroom6'),
       ('course7', 'subject7', 8, 'classroom7'),
       ('course8', 'subject8', 9, 'classroom8'),
       ('course9', 'subject9', 10, 'classroom9')
;

INSERT INTO privilege_entity (id)
VALUES ('PRIVILEGE0'),
       ('PRIVILEGE1'),
       ('PRIVILEGE2'),
       ('PRIVILEGE3'),
       ('PRIVILEGE4'),
       ('PRIVILEGE5'),
       ('PRIVILEGE6'),
       ('PRIVILEGE7'),
       ('PRIVILEGE8'),
       ('PRIVILEGE9')
;

INSERT INTO group_entity (id)
VALUES ('group0'),
       ('group1'),
       ('group2'),
       ('group3'),
       ('group4'),
       ('group5'),
       ('group6'),
       ('group7'),
       ('group8'),
       ('group9')
;

MERGE INTO group_privileges (group_id, privilege_id) VALUES
    ('group0', 'PRIVILEGE0'),
    ('group1', 'PRIVILEGE1'),
    ('group2', 'PRIVILEGE2'),
    ('group3', 'PRIVILEGE3'),
    ('group4', 'PRIVILEGE4'),
    ('group5', 'PRIVILEGE5'),
    ('group6', 'PRIVILEGE6'),
    ('group7', 'PRIVILEGE7'),
    ('group8', 'PRIVILEGE8'),
    ('group9', 'PRIVILEGE9')
;

/* Max = securestPasswordProbably123!, John = 123password! and Martin = password123*/
INSERT INTO user_entity (first_name, last_name, login_name, account_type, system_account, enabled, locked, theme_id, status, class_room_id)
VALUES ('User', '0', 'user.0', 0, FALSE, TRUE, FALSE, 1, 0, 'classroom0'),      -- Regular user, enabled, not locked, (in group0)
       ('User', '1', 'user.1', 0, FALSE, TRUE, TRUE, 1, 0, 'classroom1'),       -- Regular user, enabled, locked (in group1)
       ('User', '2', 'user.2', 0, FALSE, FALSE, FALSE, 1, 0, 'classroom2'),     -- Regular user, disabled, not locked (in group2)
       ('User', '3', 'user.3', 0, FALSE, FALSE, TRUE, 1, 0, 'classroom3'),      -- Regular user, disabled, locked (in group3)
       ('User', '4', 'user.4', 0, TRUE, TRUE, FALSE, 1, 0, 'classroom4'),       -- System account, enabled, not locked  (in group4)
       ('User', '5', 'user.5', 1, TRUE, TRUE, TRUE, 1, 0, 'classroom0'),        -- System account, enabled, locked (in group5)
       ('User', '6', 'user.6', 1, TRUE, FALSE, FALSE, 1, 0, 'classroom1'),      -- System account, disabled, not locked (in group6)
       ('User', '7', 'user.7', 1, TRUE, FALSE, TRUE, 1, 0, 'classroom2'),       -- System account, disabled, locked (in group7)
       ('User', '8', 'user.8', 1, FALSE, TRUE, FALSE, 1, 0, 'classroom3'),      -- Admin user, enabled, not locked (in group8)
       ('User', '9', 'user.9', 1, FALSE, TRUE, TRUE, 1, 0, 'classroom4'),       -- Admin user, enabled, locked (in group9)
       ('User', '10', 'user.10', 2, FALSE, FALSE, FALSE, 1, 0, 'classroom0'),   -- Admin user, disabled, not locked
       ('User', '11', 'user.11', 2, FALSE, FALSE, TRUE, 1, 0, 'classroom1'),    -- Admin user, disabled, locked
       ('User', '12', 'user.12', 2, TRUE, TRUE, FALSE, 1, 0, 'classroom2'),     -- System admin, enabled, not locked
       ('User', '13', 'user.13', 2, TRUE, TRUE, TRUE, 1, 0, 'classroom3'),      -- System admin, enabled, locked
       ('User', '14', 'user.14', 2, TRUE, FALSE, FALSE, 1, 0, 'classroom4'),    -- System admin, disabled, not locked
       ('User', '15', 'user.15', 2, TRUE, FALSE, TRUE, 1, 0, NULL),  -- System admin, disabled, locked
       ('User', '16', 'user.16', 0, FALSE, TRUE, FALSE, 1, 0, NULL), -- Test user for deletion case (enabled, not locked)
       ('User', '17', 'user.17', 1, FALSE, TRUE, FALSE, 1, 0, 'classroom8')     -- Test user for deletion case (in group0)
;

INSERT INTO course_users(user_id, course_id) VALUES (1, 1), (1, 2);

INSERT INTO credential_entity(id, method, data, secret, enabled, user_id)
VALUES (962, 0, '', '', true, 1),
       (993, 1, '', '', true, 1),
       (963, 0, '', '', false, 2),
       (964, 0, '', '', true, 3)
;

MERGE INTO user_groups (user_id, group_id) VALUES
    (1, 'group0'),
    (2, 'group1'),
    (3, 'group2'),
    (4, 'group3'),
    (5, 'group4'),
    (6, 'group5'),
    (7, 'group6'),
    (8, 'group7'),
    (9, 'group8'),
    (18, 'group0')
;

INSERT INTO message_entity (author_id, body, timestamp, status)
VALUES (1, 'Hey, wie geht es?', 1702672064, 0),
       (2, 'voll cool.', 1702672164, 0),
       (3, 'hals maul', 1702675064, 0),
       (4, 'dummy', 1802672064, 0);

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

