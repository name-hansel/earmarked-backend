DELETE FROM earmark;

ALTER TABLE earmark
    ADD COLUMN album_name VARCHAR(512) NOT NULL,
    ADD COLUMN album_release_date DATE NOT NULL;