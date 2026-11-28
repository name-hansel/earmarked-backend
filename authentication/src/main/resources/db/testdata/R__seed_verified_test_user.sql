INSERT INTO users (id, email, username, password_hash, verified)
VALUES ('11111111-1111-1111-1111-111111111111',
        'test@earmarked.local',
        'testuser',
        '$2a$10$tzNm/o5peYHEBy1vZU6NZ.xBHy.9saEx4vfQD/z8eD8qPcdhmVl.G',
        TRUE)
        ON CONFLICT (id) DO NOTHING;