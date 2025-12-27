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

ALTER TABLE study_programs
    ADD CONSTRAINT uq_study_programs_name UNIQUE (name);

-- End of V2
