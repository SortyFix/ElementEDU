/*
=============================================================================

This SQL script populates the 'user_entity', 'group_entity', 'privilege_entity',
'user_groups', and 'group_privileges' tables with test data that demonstrate
the assignment of groups and privileges to users in 3 distinct levels of access,
plus a dummy/test scenario.

This script reflects the following design pattern:

1. Users: There are four test users, each at a different level, from least to most privileged:
    - 'Max Mustermann' is at level 1, the most basic level of access
    - 'John Zimmermann' is at level 2, with slightly more access
    - 'Martin Hansen' is at level 3, having the highest level of access or broader privileges
    - Additionally, there is a 'dummy' user primarily used for testing delete operations

2. Groups: These are the roles or positions users can occupy within the application.
Every user belongs to one or more groups:
    - The 'Users' group is the basic level, associated with normal users
    - 'Moderators' occupy the middle ground in terms of access rights
    - 'Admins' possesses the highest privileges
    - 'Dummy' is a testing group designed for testing delete operations

3. Privileges: These are the specific rights or permissions granted to a group. A group
can have one or more privileges:
    - 'READ' is the most basic privilege
    - 'WRITE' is an intermediate privilege
    - 'MODERATE' is the highest privilege
    - 'DUMMY' is a test privilege for testing the delete operation

4. 'user_groups' and 'group_privileges' tables are junction tables that represent many-to-many
relationships between users and groups as well as groups and privileges.

In summary, each user has a corresponding group and privilege following a pattern from the
least to the most privileged. User 'Max' is associated with the 'Users' group and 'READ' privilege,
'Martin' with the 'Admins' group and 'MODERATE' privilege, so on and so forth. The pattern is
strictly consistent except for the last 'dummy' case meant for delete tests.

Please note that this is simulated data and does not necessarily reflect real-world users,
groups, or privileges. The main objective is to provide test data that demonstrates the
assignment and hierarchy of users, groups, and privileges.

=============================================================================
*/
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
INSERT INTO user_entity (first_name, last_name, login_name, system_account, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', FALSE, FALSE, FALSE, 1, 0),
       ('John', 'Zimmermann', 'john.zimmermann', FALSE, TRUE, TRUE, 2, 0),
       ('Martin', 'Hansen', 'martin.hansen', FALSE, TRUE, FALSE, 3, 0),
       ('dummy', 'dummy', 'dummy.dummy', FALSE, TRUE, FALSE, 2, 3),
       ('gaz', 'gaz', 'gaz', TRUE, TRUE, FALSE, 2, 3);

INSERT INTO message_entity (author_id, body, timestamp, status)
VALUES (1, 'Hey, wie geht es?', 1702672064, 0),
       (2, 'voll cool.', 1702672164, 0),
       (3, 'hals maul', 1702675064, 0),
       (4, 'dummy', 1802672064, 0);

INSERT INTO group_entity (name)
VALUES ('Users'),
       ('Moderators'),
       ('Admin'),
       ('Dummy');

INSERT INTO privilege_entity (name)
VALUES ('READ'),
       ('WRITE'),
       ('MODERATE'),
       ('DUMMY');

/* 0 = PASSWORD, 1 = EMAIL, 2 = SMS, 3 = TOTP */
INSERT INTO credential_entity(id, method, data, allowedMethods, secret, enabled, user_id)
VALUES (992, 0, '$2y$10$CsbEQdr99lfl9rWp18wJ3OKPINMuIuWzUgQR3Ek5F.Xj3rNQeD7KG', NULL, '', true, 1),
       (993, 1, 'mustermann@example.com', '', NULL, true, 1),

       (1023, 0, '$2y$10$4urnpOegHUXoQaQakLcKP.iNZxrGeaKhS.55FlAI1eJqkLeGqF.iO', '', true, 2),
       (1024, 1, 'mustermann@example.com', '', NULL, false, 2),
       (1025, 2, '555 5555555', '', NULL, true, 2),

       (1054, 0, '$2y$10$eHQ64sFwMpF0Gz4Fc2aKVuRoND6v78AAx/Oplh.uVBYLIGATUnBQq', '', true, 3),
       (1055, 1, 'mustermann@examle.com', '', NULL, true, 3),
       (1056, 2, '555 5555555', '', NULL, true, 3),
       (1057, 3, NULL, '', NULL, false, 3),

       (1088, 3, NULL, '', NULL, true, 4);

--- Courses
INSERT INTO subject_entity(name)
VALUES ('German'),
       ('Mathematics'),
       ('Informatics'),
       ('Dummy');

INSERT INTO course_entity(name, subject_id, class_room_id)
VALUES ('Q1-German', 1, 1),
       ('5e-Math', 2, 1),
       ('2e-Informatics', 3, 3),
       ('Dummy', 4, 4);
---

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

MERGE INTO chat_entity_messages (chat_id, message_id) VALUES (1, 1), (2, 2), (3, 3), (4, 4);

MERGE INTO chat_entity_users (chat_id, user_id) VALUES (1, 1), (1, 3), (2, 2), (2, 4), (3, 2), (3, 3), (4, 1), (4, 4);

MERGE INTO user_groups (user_id, group_id) VALUES (1, 1), (2, 1), (2, 2), (3, 1), (3, 2), (3, 3), (4, 4);

MERGE INTO group_privileges (group_id, privilege_id) VALUES (1, 1), (2, 1), (2, 2), (3, 1), (3, 2), (3, 3), (4, 4);

MERGE INTO course_users (course_id, user_id) VALUES (2, 2), (3, 1), (4, 4);

MERGE INTO class_room_users (class_room_id, user_id) VALUES (1, 1), (2, 2), (2, 3), (4, 4);
