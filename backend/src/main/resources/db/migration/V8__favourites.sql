-- =====================================================
-- V8: Favourites
-- =====================================================
-- User favourite courses.
-- =====================================================

SET search_path TO app;

CREATE TABLE favourites (
    user_id     UUID NOT NULL,
    course_id   UUID NOT NULL,

    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, course_id)
);

-- Index
CREATE INDEX idx_favourites_course ON favourites (course_id);

-- FK
ALTER TABLE favourites
ADD CONSTRAINT fk_favourites_course
FOREIGN KEY (course_id)
REFERENCES courses (id)
ON DELETE CASCADE;