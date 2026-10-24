ALTER TABLE earmark
    ADD COLUMN listened_at DATE NULL;

CREATE INDEX idx_earmarks_user_listened
    ON earmark (user_id, listened_at);