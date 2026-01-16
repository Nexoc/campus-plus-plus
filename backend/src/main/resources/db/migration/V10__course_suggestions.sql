-- =====================================================
-- V10__course_suggestions.sql
-- =====================================================
-- User-submitted suggestions for new courses
-- =====================================================

SET search_path TO app;

CREATE TABLE course_suggestions (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,

    title       VARCHAR(255) NOT NULL,
    description TEXT,

    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_course_suggestions_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

    CONSTRAINT uq_course_suggestions_user_title
        UNIQUE (user_id, title)
);

CREATE INDEX idx_course_suggestions_status
    ON course_suggestions (status);

CREATE INDEX idx_course_suggestions_user
    ON course_suggestions (user_id);
