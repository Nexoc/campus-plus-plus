-- =====================================================
-- V3: Courses
-- =====================================================
-- Creates the base table for courses.
-- No relations to study programs or other domains
-- are defined in this migration.
-- =====================================================

SET search_path TO app;

-- -----------------------------
-- Courses
-- -----------------------------
CREATE TABLE courses (
    id              UUID PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    ects            INTEGER NOT NULL,
    abbreviation    VARCHAR(50),
    language        VARCHAR(50) NOT NULL,

    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -----------------------------
-- Constraints
-- -----------------------------
ALTER TABLE courses
ADD CONSTRAINT chk_courses_ects_positive
CHECK (ects > 0);

-- =====================================================
-- End of V3
-- =====================================================
