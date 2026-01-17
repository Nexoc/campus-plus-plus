-- =====================================================
-- V16: Comments
-- =====================================================
-- Comments inside discussion posts.
-- =====================================================

SET search_path TO app;

CREATE TABLE comments (
    id          UUID PRIMARY KEY,
    post_id     UUID NOT NULL,
    user_id     UUID NOT NULL,

    content     TEXT NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_comments_post ON comments (post_id);
CREATE INDEX idx_comments_user ON comments (user_id);

-- FK
ALTER TABLE comments
ADD CONSTRAINT fk_comments_post
FOREIGN KEY (post_id)
REFERENCES posts (id)
ON DELETE CASCADE;
