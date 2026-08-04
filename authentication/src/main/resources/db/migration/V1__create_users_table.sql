CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(320) NOT NULL UNIQUE,
    username      VARCHAR(16)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    verified      BOOLEAN      NOT NULL DEFAULT FALSE
);