-- =====================================================
-- V8__comments.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE comments (
    id          UUID PRIMARY KEY,
    post_id     UUID NOT NULL,
    user_id     UUID NOT NULL,

    user_name   VARCHAR(255),
    content     TEXT NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_comments_post
    ON comments (post_id);

CREATE INDEX idx_comments_user
    ON comments (user_id);

CREATE INDEX idx_comments_user_name
    ON comments (user_name);
