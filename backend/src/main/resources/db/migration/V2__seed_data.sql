INSERT INTO users (id, name, email, password_hash, role)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Noizy Admin', 'admin@noizy.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

INSERT INTO artists (id, name, description, image_url)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'Solar Drift', 'Instrumental electronic music with bright synth layers.', 'seed/images/solar-drift.jpg'),
    ('10000000-0000-0000-0000-000000000002', 'Marta Vale', 'Indie pop songwriter focused on intimate arrangements.', 'seed/images/marta-vale.jpg'),
    ('10000000-0000-0000-0000-000000000003', 'Northline Trio', 'Jazz trio blending piano, bass, and soft percussion.', 'seed/images/northline-trio.jpg')
ON CONFLICT DO NOTHING;

INSERT INTO albums (id, title, artist_id, cover_url, release_date)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Golden Hour Signals', '10000000-0000-0000-0000-000000000001', 'seed/images/golden-hour-signals.jpg', '2025-02-14'),
    ('20000000-0000-0000-0000-000000000002', 'Rooms With Sunlight', '10000000-0000-0000-0000-000000000002', 'seed/images/rooms-with-sunlight.jpg', '2024-09-05'),
    ('20000000-0000-0000-0000-000000000003', 'Late Platform', '10000000-0000-0000-0000-000000000003', 'seed/images/late-platform.jpg', '2023-11-18')
ON CONFLICT DO NOTHING;

INSERT INTO tracks (id, title, artist_id, album_id, genre, duration_seconds, audio_s3_key, cover_s3_key, play_count)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'Dawn Circuit', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Electronic', 194, 'seed/audio/dawn-circuit.mp3', 'seed/images/golden-hour-signals.jpg', 0),
    ('30000000-0000-0000-0000-000000000002', 'Signal Bloom', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Electronic', 211, 'seed/audio/signal-bloom.mp3', 'seed/images/golden-hour-signals.jpg', 0),
    ('30000000-0000-0000-0000-000000000003', 'Window Notes', '10000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'Indie Pop', 178, 'seed/audio/window-notes.mp3', 'seed/images/rooms-with-sunlight.jpg', 0),
    ('30000000-0000-0000-0000-000000000004', 'Platform Six', '10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', 'Jazz', 243, 'seed/audio/platform-six.mp3', 'seed/images/late-platform.jpg', 0)
ON CONFLICT DO NOTHING;

INSERT INTO playlists (id, name, description, owner_id, is_public)
VALUES
    ('40000000-0000-0000-0000-000000000001', 'Yellow Room', 'A warm starter playlist for Noizy demos.', '00000000-0000-0000-0000-000000000001', true)
ON CONFLICT DO NOTHING;

INSERT INTO playlist_tracks (playlist_id, track_id, position)
VALUES
    ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 1),
    ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000003', 2),
    ('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000004', 3)
ON CONFLICT DO NOTHING;
