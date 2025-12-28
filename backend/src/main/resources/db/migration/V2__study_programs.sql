-- =====================================================
-- V2: Study programs
-- =====================================================
-- Defines study_programs with program attributes.
-- =====================================================

SET search_path TO app;

CREATE TABLE study_programs (
    id               UUID PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    degree           VARCHAR(100),
    semesters        INTEGER,
    mode             VARCHAR(50),
    total_ects       INTEGER,
    language         VARCHAR(100),
    application_period TEXT,
    start_dates      TEXT,
    source_url       VARCHAR(500),

    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add unique constraint for (name, mode) to allow multiple programs with same name but different modes
ALTER TABLE study_programs
ADD CONSTRAINT study_programs_name_mode_unique UNIQUE (name, mode);

-- End of V2
