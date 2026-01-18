-- =====================================================
-- V10__reactions.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE reactions (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,
    target_type     VARCHAR(50) NOT NULL,
    target_id       UUID NOT NULL,
    reaction_type   VARCHAR(50) NOT NULL DEFAULT 'LIKE',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_reactions_user_target
        UNIQUE (user_id, target_type, target_id, reaction_type),

    CONSTRAINT chk_reactions_target_type
        CHECK (target_type IN ('POST', 'REVIEW'))
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_reactions_target
    ON reactions (target_type, target_id);

CREATE INDEX idx_reactions_user
    ON reactions (user_id);
