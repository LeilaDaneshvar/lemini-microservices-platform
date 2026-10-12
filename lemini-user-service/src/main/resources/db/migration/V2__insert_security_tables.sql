INSERT INTO roles (name)
VALUES
    ('ADMIN'),
    ('USER');

INSERT INTO authorities (name)
VALUES
    ('READ'),
    ('WRITE'),
    ('DELETE'),
    ('UPDATE');

INSERT INTO role_authorities (role_id, authority_id)
VALUES
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM authorities WHERE name = 'READ')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM authorities WHERE name = 'WRITE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM authorities WHERE name = 'DELETE')),
    ((SELECT id FROM roles WHERE name = 'ADMIN'), (SELECT id FROM authorities WHERE name = 'UPDATE')),
    ((SELECT id FROM roles WHERE name = 'USER'), (SELECT id FROM authorities WHERE name = 'READ')),
    ((SELECT id FROM roles WHERE name = 'USER'), (SELECT id FROM authorities WHERE name = 'WRITE')),
    ((SELECT id FROM roles WHERE name = 'USER'), (SELECT id FROM authorities WHERE name = 'UPDATE'));