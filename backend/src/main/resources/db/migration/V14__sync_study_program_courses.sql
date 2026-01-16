-- =====================================================
-- V14__sync_study_program_courses.sql
-- =====================================================
-- Sync study_program_id from study_program_courses
-- to courses table for any records that are still NULL
-- =====================================================

SET search_path TO app;

-- Update courses with study_program_id from join table
UPDATE courses c
SET study_program_id = (
    SELECT study_program_id
    FROM study_program_courses spc
    WHERE spc.course_id = c.id
    LIMIT 1
)
WHERE c.study_program_id IS NULL
  AND EXISTS (
    SELECT 1
    FROM study_program_courses spc
    WHERE spc.course_id = c.id
);

-- =====================================================
-- End of V14
-- =====================================================
