-- =====================================================
-- V12: Study Program Favourites
-- =====================================================
-- User favourite study programs.
-- =====================================================

SET search_path TO app;

CREATE TABLE study_program_favourites (
    user_id             UUID NOT NULL,
    study_program_id    UUID NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, study_program_id)
);

-- Index
CREATE INDEX idx_study_program_favourites_program ON study_program_favourites (study_program_id);

-- Foreign key to study_programs with CASCADE delete
ALTER TABLE study_program_favourites 
ADD CONSTRAINT fk_study_program_favourites_program
FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE CASCADE;
