
-- User
CREATE TABLE users (
    id                      UUID                        NOT NULL,
    username                TEXT                 UNIQUE NOT NULL,
    email                   TEXT                 UNIQUE NOT NULL,
    first_name              TEXT                        NOT NULL,
    last_name               TEXT                        NOT NULL,
    phone                   TEXT,
    phone_country           TEXT,
    role                    TEXT,
    password                TEXT                        NOT NULL,
    salt                    TEXT                        NOT NULL,
    user_type               TEXT                        NOT NULL,
    created_on              TIMESTAMP                   NOT NULL,
    is_active               BOOLEAN                     NOT NULL DEFAULT FALSE,
    activation_token        UUID,
    activation_expires_on   TIMESTAMP,

    CONSTRAINT user_primary_key PRIMARY KEY (id)
);
CREATE UNIQUE INDEX users_activation_token ON users (email, activation_token);

-- Token
CREATE TABLE token (
    id                  UUID                        NOT NULL,
    user_id             UUID                        NOT NULL,
    token_type          TEXT                        NOT NULL,
    created_on          TIMESTAMP                   NOT NULL,
    expires_on          TIMESTAMP                   NOT NULL,

    CONSTRAINT token_primary_key PRIMARY KEY (id),
    FOREIGN KEY (user_id)        REFERENCES users(id)
);
CREATE INDEX token_user_id ON token (user_id);