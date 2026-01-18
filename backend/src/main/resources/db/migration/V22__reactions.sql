-- =====================================================
-- V22: Reactions
-- =====================================================
-- User reactions (likes) for posts and reviews
-- =====================================================

SET search_path TO app;

CREATE TABLE reactions (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,
    target_type     VARCHAR(50) NOT NULL,  -- 'POST' or 'REVIEW'
    target_id       UUID NOT NULL,
    reaction_type   VARCHAR(50) NOT NULL DEFAULT 'LIKE',  -- Currently only 'LIKE', extensible for future
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Constraint: a user can react only once per target per reaction type
    CONSTRAINT uq_reactions_user_target UNIQUE (user_id, target_type, target_id, reaction_type),
    
    -- Ensure target_type is valid
    CONSTRAINT chk_reactions_target_type CHECK (target_type IN ('POST', 'REVIEW'))
);

-- Indexes for performance
CREATE INDEX idx_reactions_target ON reactions (target_type, target_id);
CREATE INDEX idx_reactions_user ON reactions (user_id);
