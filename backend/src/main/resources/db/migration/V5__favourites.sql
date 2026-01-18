-- =====================================================
-- V5__favourites.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE favourites (
    user_id     UUID NOT NULL,
    course_id   UUID NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, course_id),

    CONSTRAINT fk_favourites_course
        FOREIGN KEY (course_id)
        REFERENCES courses (id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_favourites_course
    ON favourites (course_id);
