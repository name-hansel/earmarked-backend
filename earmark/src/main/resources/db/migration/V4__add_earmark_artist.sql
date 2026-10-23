DELETE
FROM earmark;

ALTER TABLE earmark
    ADD COLUMN album_artists VARCHAR(1024) NOT NULL;