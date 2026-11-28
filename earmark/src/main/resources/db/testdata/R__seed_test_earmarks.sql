INSERT INTO earmarks (id, user_id, spotify_album_id, album_name, album_artists,
                      album_release_date, note, created_at, listened, listened_at,
                      rating, review)
SELECT gen_random_uuid(),
       '11111111-1111-1111-1111-111111111111'::uuid,
       v.album_id, v.album_name, v.album_artists, v.release_date::date, v.note,
       now() - make_interval(days => v.created_days_ago),
       v.listened,
       CASE WHEN v.listened_days_ago IS NULL THEN NULL
            ELSE current_date - v.listened_days_ago END,
       v.rating, v.review
FROM (VALUES
  ('0JICUfSKOdGqzE9FJSrNci', 'Dopamine Chamber', 'Fontaines D.C.', '2026-10-16', 'New release', 4, false, NULL, NULL, NULL),
  ('4N31AIbwVZbbfDe48xbGnY', 'Stray Dogs', 'Nothing But Thieves', '2026-09-25', 'Check out after the tour announcement', 2, false, NULL, NULL, NULL),
  ('1qSS0T6Ffrb3rFVpizzOuk', 'U', 'underscores', '2026-03-20', NULL, 5, false, NULL, NULL, NULL),
  ('6x0nmCMjHeuN3rYBmI5Bor', 'Reality Awaits', 'The Strokes', '2026-07-24', NULL, 7, false, NULL, NULL, NULL),
  ('2LgXUDNp1uPV0t55aTlfUT', 'This Mirror Weighs a Ton', 'Interpol', '2026-08-28', NULL, 9, true, 3, 3,
   'Interpol sound sharp and confident here without leaning on nostalgia. The guitars are tight, the rhythm section drives everything, and the moodiness feels earned rather than performed.

It took two listens to settle in, but after that I kept coming back to it. Easily one of my favourite releases of the year.'),
  ('4sW8Eql2e2kdRP1A1R1clG', 'Turn On The Bright Lights', 'Interpol', '2002-08-20', 'Classic, finally getting to it', 12, true, 6, 3,
   'Finally understood why people talk about this one so much. The whole album feels like a single late-night mood: cold, tense and strangely romantic. The production is dry and spare, which makes the guitars and vocals hit harder.

I would not pick a standout, because it works best front to back. Best listened to at night with nothing else going on.'),
  ('2noRn2Aes5aoNVsU6iWThc', 'Discovery', 'Daft Punk', '2001-03-12', NULL, 15, true, NULL, 2, NULL),
  ('0eeXb23yMW6EaIgm63xxPC', 'Getting Killed', 'Geese', '2025-09-26', 'Everyone keeps recommending this', 18, false, NULL, NULL, NULL),
  ('7mOrnQqDad3RgYQsJGaaqk', 'Heavy Metal', 'Cameron Winter', '2024-12-06', NULL, 21, true, 14, 1,
   'I wanted to like this more than I did. The rawness is clearly intentional, and there are moments of real beauty, but the rest felt scattered and unfinished to me.

Respect the ambition, and I can see why it has such devoted fans. It just was not for me, and I do not see myself replaying it.'),
  ('4w3NeXtywU398NYW4903rY', 'Songs For The Deaf', 'Queens of the Stone Age', '2002-01-01', NULL, 24, false, NULL, NULL, NULL),
  ('7a7arAXDE0BiaMgHLhdjGF', 'The Downward Spiral', 'Nine Inch Nails', '1994-03-08', 'Listen with headphones', 27, true, 20, 3,
   'Headphones were the right call. The layering and the noise textures reveal themselves slowly, and the quiet moments land even harder next to the aggressive ones.

It is heavy and often uncomfortable, but it is also carefully built. The pacing across the full album is what makes it work, so skip shuffle.

Not an easy listen, but a rewarding one.'),
  ('0l8zYqoUeBYg47Gmevq9HZ', 'The Art of Loving', 'Olivia Dean', '2025-09-26', NULL, 30, false, NULL, NULL, NULL),
  ('0tc6zo8FGuGegfZIicLEwV', 'Emotion', 'Carly Rae Jepsen', '2015-08-21', NULL, 33, true, 25, 2,
   'Pure pop craft. Every song is built around a big hook, and the production is glossy and polished without feeling cold. I enjoyed it a lot on the first pass.

It runs a bit long, and the middle blurs together for me, which is why it is not a perfect score. Still a great one to put on when I want something fun and upbeat.'),
  ('50ZenUP4O2Q5eCy2NRNvuz', 'Stick Season', 'Noah Kahan', '2022-10-14', NULL, 36, true, NULL, NULL, NULL),
  ('0TSynllAnze7KMhv6J7W18', 'The Great Divide: The Last Of The Bugs', 'Noah Kahan', '2026-04-25', 'Start with the singles', 38, false, NULL, NULL, NULL),
  ('6HbJlAnTRhWae1F3lEwGkv', 'An Awesome Wave', 'alt-J', '2012-05-28', NULL, 41, true, 30, 0,
   'I really tried with this one. The inventive arrangements and unusual vocals are impressive on paper, but I never connected with them emotionally.

The vocal style wore on me quickly, and the quirkiness felt more like a gimmick than a mood. Interesting, just not my taste.'),
  ('2PfGKHtqEX58bHtkQxJnWG', 'Love Deluxe', 'Sade', '1992-10-26', NULL, 44, false, NULL, NULL, NULL),
  ('6dVIqQ8qmQ5GBnJ9shOYGE', 'OK Computer', 'Radiohead', '1997-05-28', NULL, 47, true, 40, 3,
   'Lives up to the reputation. The mix of anxiety and beauty is hard to describe, and the arrangements keep revealing new details across listens.

What stands out most is how cohesive it is. The sound shifts a lot from song to song, yet it feels like one world. I expect to keep returning to this one for years.'),
  ('1zNtJFMCNIyT0X19jpcI3j', 'Friends That Break Your Heart', 'James Blake', '2021-10-08', 'Recommended by a friend', 50, true, 45, 2,
   'Glad I took the recommendation. Quiet, introspective and quite beautiful at its best, with production that leaves plenty of space around the vocals.

It is slow and a bit samey in places, so it is more of a late-evening album than something for active listening. The better stretches pull it up to a 2.'),
  ('6idVoBWP2mt1qoMtASm3gc', 'TA13OO', 'Denzel Curry', '2018-07-27', NULL, 53, true, NULL, 0, NULL),
  ('3Ge6S7DSl3XH69XR3Cg6T6', 'Strictly 4 The Scythe', 'Denzel Curry, The Scythe', '2026-03-06', 'Heard the lead single, sounds good', 56, false, NULL, NULL, NULL),
  ('5Asm8hPfn7pAlxpoir4ljs', 'Cry Baby', 'Vince Staples', '2026-06-05', NULL, 58, true, 50, 1,
   'Not what I expected. The production is interesting and the delivery is confident, but the songs did not stick with me after the album ended.

A few moments stood out, and I might revisit those individually. As a whole it did not hold my attention.')
) AS v(album_id, album_name, album_artists, release_date, note,
       created_days_ago, listened, listened_days_ago, rating, review)
ON CONFLICT (user_id, spotify_album_id) DO NOTHING;