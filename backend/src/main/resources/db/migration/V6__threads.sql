-- =====================================================
-- V6__threads.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE threads (
    id              UUID PRIMARY KEY,
    course_id       UUID NOT NULL,
    title           VARCHAR(255) NOT NULL,

    created_by      UUID,
    created_by_name VARCHAR(255),
    content         TEXT,

    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_threads_course
        FOREIGN KEY (course_id)
        REFERENCES courses (id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_threads_course
    ON threads (course_id);

CREATE INDEX idx_threads_created_by
    ON threads (created_by);

CREATE INDEX idx_threads_created_by_name
    ON threads (created_by_name);
