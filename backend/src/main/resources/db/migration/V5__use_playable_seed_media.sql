UPDATE artists
SET image_url = replace(image_url, '.jpg', '.svg')
WHERE image_url LIKE 'seed/images/%.jpg';

UPDATE albums
SET cover_url = replace(cover_url, '.jpg', '.svg')
WHERE cover_url LIKE 'seed/images/%.jpg';

UPDATE tracks
SET
    audio_s3_key = replace(audio_s3_key, '.mp3', '.wav'),
    cover_s3_key = replace(cover_s3_key, '.jpg', '.svg'),
    duration_seconds = 3
WHERE audio_s3_key LIKE 'seed/audio/%.mp3';
