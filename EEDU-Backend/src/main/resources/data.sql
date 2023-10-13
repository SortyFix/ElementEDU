/*
 * This file is for testing purposes only.
 * It provides test users and inserts them into the database
 */
INSERT INTO user_entity (first_name, last_name, login_name, password, enabled, locked)

VALUES ('Max', 'Mustermann', 'max.mustermann', 'password123', TRUE, FALSE),
       ('John', 'Zimmermann', 'john.zimmermann', 'password123', TRUE, TRUE),
       ('Martin', 'Hansen', 'martin.hansen', 'password123', FALSE, FALSE),
       ('Andrew', 'Smith', 'andrew.smith', 'password123', TRUE, FALSE),
       ('Emma', 'Brown', 'emma.brown', 'password123', TRUE, FALSE),
       ('Oliver', 'Taylor', 'oliver.taylor', 'password123', FALSE, TRUE),
       ('Sophia', 'Evans', 'sophia.evans', 'password123', TRUE, FALSE),
       ('Liam', 'Wilson', 'liam.wilson', 'password123', TRUE, FALSE),
       ('Lucas', 'Thomas', 'lucas.thomas', 'password123', FALSE, TRUE),
       ('Ava', 'Roberts', 'ava.roberts', 'password123', TRUE, TRUE);

/*
 * Inserting test data into privilege_entities table.
 */
INSERT INTO group_entity (name)
VALUES ('Admins'),
       ('Users');

/*
 * Inserting test data into privilege_entities table.
 */
INSERT INTO privilege_entity (name)
VALUES ('READ'),
       ('WRITE'),
       ('DELETE');

/*
 * Inserting user-groups relations.
 * Assume that users with id 1, 2, 4, and 5 belong to the 'Admins' group (id 1),
 * and users 3, 6, 7, 8, 9, and 10 belong to 'Users' group (id 2)
 */
INSERT INTO user_groups (user_id, group_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 1),
       (5, 1),
       (6, 2),
       (7, 2),
       (8, 2),
       (9, 2),
       (10, 2);

/*
 * Inserting group-privileges relations.
 * Assume that the 'Admins' group (id 1) has all the privileges (READ, WRITE, DELETE),
 * and the 'Users' group (id 2) only has the READ privilege.
 */
INSERT INTO group_privileges (group_id, privilege_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1);