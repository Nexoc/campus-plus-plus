-- =====================================================
-- V2__study_programs.sql
-- =====================================================
-- Defines study programs with program attributes
-- =====================================================

SET search_path TO app;

CREATE TABLE study_programs (
    id                 UUID PRIMARY KEY,

    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    degree             VARCHAR(100),
    semesters          INTEGER,
    mode               VARCHAR(50),
    total_ects         INTEGER,
    language           VARCHAR(100),
    application_period TEXT,
    start_dates        TEXT,
    source_url         VARCHAR(500),

    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),

    -- Allow same program name in different study modes
    CONSTRAINT uq_study_programs_name_mode
        UNIQUE (name, mode)
);
