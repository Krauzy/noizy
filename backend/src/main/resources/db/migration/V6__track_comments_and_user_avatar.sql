ALTER TABLE users
    ADD COLUMN avatar_s3_key TEXT;

CREATE TABLE track_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body VARCHAR(1200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_track_comments_track_created ON track_comments (track_id, created_at DESC);
CREATE INDEX idx_track_comments_user_id ON track_comments (user_id);
