ALTER TABLE earmark
    ADD COLUMN listened BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE earmark
SET listened = TRUE
WHERE listened_at IS NOT NULL;

ALTER TABLE earmark
    ADD CONSTRAINT chk_earmarks_listened_at
        CHECK (listened OR listened_at IS NULL);

DROP INDEX idx_earmarks_user_listened;
CREATE INDEX idx_earmarks_user_listened ON earmark (user_id, listened);