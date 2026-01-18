-- =====================================================
-- V4__reviews.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE reviews (
    id                  UUID PRIMARY KEY,

    user_id             UUID NOT NULL,
    course_id           UUID NOT NULL,

    rating              INTEGER NOT NULL,
    difficulty          INTEGER,
    workload            INTEGER,
    satisfaction        INTEGER,

    prior_requirements  TEXT,
    exam_info           TEXT,
    text                TEXT,

    moderation_flagged  BOOLEAN DEFAULT false,
    moderation_reason   TEXT,

    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_reviews_rating_range
        CHECK (rating BETWEEN 1 AND 5),

    CONSTRAINT fk_reviews_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_reviews_course
    ON reviews (course_id);

CREATE INDEX idx_reviews_user
    ON reviews (user_id);

CREATE INDEX idx_reviews_moderation_flagged
    ON reviews (moderation_flagged);
