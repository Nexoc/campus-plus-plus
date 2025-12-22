-- =====================================================
-- V10: Course suggestions
-- =====================================================
-- User-submitted suggestions for new courses.
-- =====================================================

SET search_path TO app;

CREATE TABLE course_suggestions (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,

    title           VARCHAR(255) NOT NULL,
    description     TEXT,

    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    submitted_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Constraints
ALTER TABLE course_suggestions
ADD CONSTRAINT chk_course_suggestions_status
CHECK (status IN ('PENDING','APPROVED','REJECTED'));

-- Indexes
CREATE INDEX idx_course_suggestions_status
ON course_suggestions (status);
