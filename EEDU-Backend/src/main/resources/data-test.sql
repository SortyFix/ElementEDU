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
INSERT INTO theme_entity(name, background_color, widget_color, text_color)
VALUES ('Light', 0x000000, 0x000000, 0x000000),
       ('Medium', 0x000000, 0x000000, 0x000000),
       ('Dark', 0x000000, 0x000000, 0x000000),
       ('dummy', 0x000000, 0x000000, 0x000000);

INSERT INTO user_entity (first_name, last_name, login_name, password, enabled, locked, theme_id, status)
VALUES ('Max', 'Mustermann', 'max.mustermann', 'password123', TRUE, FALSE, 1, 0),
       ('John', 'Zimmermann', 'john.zimmermann', 'password123', TRUE, TRUE, 2, 0),
       ('Martin', 'Hansen', 'martin.hansen', 'password123', FALSE, FALSE, 3, 0),
       ('dummy', 'dummy', 'dummy.dummy', 'password123', TRUE, FALSE, 4, 3);

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

INSERT INTO two_factor_entity(method, data, secret, enabled, user_id)
VALUES (0, 'mustermann@example.com', '', true, 1),

       (0, 'mustermann@example.com', '', true, 2),
       (1, '555 5555555', '', true, 2),

       (0, 'mustermann@examle.com', '', true, 3),
       (1, '555 5555555', '', true, 3),
       (2, NULL, '', true, 3),

       (2, NULL, '', true, 4);

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
