INSERT INTO user_details (username, password, authorities)
VALUES ('user1', 'password1', 'ROLE_USER')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_details (username, password, authorities)
VALUES ('user2', 'password2', 'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;
