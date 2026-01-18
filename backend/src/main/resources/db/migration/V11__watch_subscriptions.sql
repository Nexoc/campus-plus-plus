-- =====================================================
-- V11__watch_subscriptions.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE watch_subscriptions (
    id                      UUID PRIMARY KEY,
    user_id                 UUID NOT NULL,
    target_type             VARCHAR(50) NOT NULL,
    target_id               UUID NOT NULL,
    notifications_enabled   BOOLEAN NOT NULL DEFAULT true,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_watch_user_target
        UNIQUE (user_id, target_type, target_id),

    CONSTRAINT chk_watch_target_type
        CHECK (target_type IN ('COURSE', 'THREAD'))
);

CREATE TABLE notification_settings (
    user_id         UUID PRIMARY KEY,
    email_enabled   BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_watch_subscriptions_user
    ON watch_subscriptions (user_id);

CREATE INDEX idx_watch_subscriptions_target
    ON watch_subscriptions (target_type, target_id);
