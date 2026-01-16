-- =====================================================
-- V6: Threads
-- =====================================================
-- Discussion threads related to courses.
-- =====================================================

SET search_path TO app;

CREATE TABLE threads (
    id          UUID PRIMARY KEY,
    course_id   UUID NOT NULL,
    title       VARCHAR(255) NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_threads_course ON threads (course_id);

-- FK
ALTER TABLE threads
ADD CONSTRAINT fk_threads_course
FOREIGN KEY (course_id)
REFERENCES courses (id)
ON DELETE CASCADE;