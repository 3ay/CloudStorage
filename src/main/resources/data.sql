INSERT INTO user_details (login, password, authorities)
VALUES ('user1', 'password1', 'ROLE_USER')
ON CONFLICT (login) DO NOTHING;

INSERT INTO user_details (login, password, authorities)
VALUES ('user2', 'password2', 'ROLE_ADMIN')
ON CONFLICT (login) DO NOTHING;
