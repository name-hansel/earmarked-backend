CREATE TABLE earmark
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id          UUID        NOT NULL,
    spotify_album_id VARCHAR(32) NOT NULL,
    note             TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_earmark_user_album UNIQUE (user_id, spotify_album_id)
);