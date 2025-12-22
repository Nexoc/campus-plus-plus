-- =====================================================
-- V4: Study programs ↔ Courses (many-to-many relation)
-- =====================================================
-- Creates join table between study_programs and courses.
-- =====================================================

SET search_path TO app;

-- -----------------------------
-- Join table
-- -----------------------------
CREATE TABLE study_program_courses (
    study_program_id UUID NOT NULL,
    course_id        UUID NOT NULL,

    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (study_program_id, course_id)
);

-- -----------------------------
-- Foreign keys
-- -----------------------------
ALTER TABLE study_program_courses
ADD CONSTRAINT fk_spc_study_program
FOREIGN KEY (study_program_id)
REFERENCES study_programs (id)
ON DELETE CASCADE;

ALTER TABLE study_program_courses
ADD CONSTRAINT fk_spc_course
FOREIGN KEY (course_id)
REFERENCES courses (id)
ON DELETE CASCADE;

-- -----------------------------
-- Indexes (for lookups)
-- -----------------------------
CREATE INDEX idx_spc_study_program
ON study_program_courses (study_program_id);

CREATE INDEX idx_spc_course
ON study_program_courses (course_id);

-- =====================================================
-- End of V4
-- =====================================================
