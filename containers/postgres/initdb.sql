CREATE TABLE IF NOT EXISTS messages
(
    id   VARCHAR(60) DEFAULT gen_random_uuid()::VARCHAR(60) PRIMARY KEY,
    text VARCHAR NOT NULL
    );

CREATE TABLE IF NOT EXISTS todos
(
    id   BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    completed   BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
