-- =====================================================
-- V2: Study programs
-- =====================================================
-- Creates tables for study programs.
-- No course relations are defined here yet.
-- =====================================================

SET search_path TO app;

-- -----------------------------
-- Study programs
-- -----------------------------
CREATE TABLE study_programs (
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,

    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -----------------------------
-- Constraints
-- -----------------------------
ALTER TABLE study_programs
ADD CONSTRAINT uq_study_programs_name UNIQUE (name);

-- =====================================================
-- End of V2
-- =====================================================
