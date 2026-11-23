ALTER TABLE earmark ADD COLUMN review VARCHAR(256);

ALTER TABLE earmark
    ADD CONSTRAINT chk_earmark_review_requires_listened
    CHECK (review IS NULL OR listened);