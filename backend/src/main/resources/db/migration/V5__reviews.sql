-- =====================================================
-- V5: Reviews
-- =====================================================
-- Reviews written by users for courses.
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

    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Constraints
ALTER TABLE reviews
ADD CONSTRAINT chk_reviews_rating_range
CHECK (rating BETWEEN 1 AND 5);

-- Indexes
CREATE INDEX idx_reviews_course ON reviews (course_id);
CREATE INDEX idx_reviews_user ON reviews (user_id);

-- FK (course only, user is external)
ALTER TABLE reviews
ADD CONSTRAINT fk_reviews_course
FOREIGN KEY (course_id)
REFERENCES courses (id)
ON DELETE CASCADE;
