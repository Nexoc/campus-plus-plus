-- =====================================================
-- V11__add_study_program_to_courses.sql
-- =====================================================
-- Change from many-to-many to many-to-one relationship
-- Add study_program_id directly to courses table
-- =====================================================

SET search_path TO app;

-- Add study_program_id column to courses
ALTER TABLE courses
    ADD COLUMN study_program_id UUID;

-- Migrate data from join table to courses
-- (for courses with multiple study programs, this picks one arbitrarily)
UPDATE courses c
SET study_program_id = (
    SELECT study_program_id
    FROM study_program_courses spc
    WHERE spc.course_id = c.id
    LIMIT 1
);

-- Add foreign key constraint
ALTER TABLE courses
    ADD CONSTRAINT fk_courses_study_program
    FOREIGN KEY (study_program_id)
    REFERENCES study_programs(id)
    ON DELETE SET NULL;

-- Create index for efficient lookups
CREATE INDEX idx_courses_study_program
    ON courses (study_program_id);

-- Keep the study_program_courses table for importer compatibility
-- (maintains the many-to-many relationship alongside the many-to-one)

-- =====================================================
-- End of V11
-- =====================================================
