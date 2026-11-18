ALTER TABLE earmark
    ADD COLUMN rating SMALLINT;

ALTER TABLE earmark
    ADD CONSTRAINT chk_earmark_rating_range CHECK (rating IS NULL OR rating BETWEEN 0 AND 3);

ALTER TABLE earmark
    ADD CONSTRAINT chk_earmark_rating_requires_listened CHECK (rating IS NULL OR listened);