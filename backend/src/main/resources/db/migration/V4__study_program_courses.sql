-- =====================================================
-- V4__study_program_courses.sql
-- =====================================================
-- Join table between study_programs and courses
-- (many-to-many relationship)
-- =====================================================

SET search_path TO app;

CREATE TABLE study_program_courses (
    study_program_id UUID NOT NULL,
    course_id        UUID NOT NULL,

    created_at       TIMESTAMP NOT NULL DEFAULT now(),

    -- Composite primary key prevents duplicates
    CONSTRAINT pk_study_program_courses
        PRIMARY KEY (study_program_id, course_id),

    -- Relations
    CONSTRAINT fk_spc_study_program
        FOREIGN KEY (study_program_id)
        REFERENCES study_programs(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_spc_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

-- Indexes for efficient lookups
CREATE INDEX idx_spc_study_program
    ON study_program_courses (study_program_id);

CREATE INDEX idx_spc_course
    ON study_program_courses (course_id);

-- =====================================================
-- End of V4
-- =====================================================

