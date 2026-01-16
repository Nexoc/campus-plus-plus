-- =====================================================
-- V7: Posts
-- =====================================================
-- Posts inside discussion threads.
-- =====================================================

SET search_path TO app;

CREATE TABLE posts (
    id          UUID PRIMARY KEY,
    thread_id   UUID NOT NULL,
    user_id     UUID NOT NULL,

    content     TEXT NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_posts_thread ON posts (thread_id);
CREATE INDEX idx_posts_user ON posts (user_id);

-- FK
ALTER TABLE posts
ADD CONSTRAINT fk_posts_thread
FOREIGN KEY (thread_id)
REFERENCES threads (id)
ON DELETE CASCADE;