-- =====================================================
-- V2__study_programs.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
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

CREATE TABLE modules (
    id                 UUID PRIMARY KEY,
    study_program_id   UUID NOT NULL,

    title              VARCHAR(255) NOT NULL,
    semester           INTEGER,

    details_html       TEXT,
    content            JSONB,
    learning_outcomes  JSONB,
    teaching_method    JSONB,
    exam_method        JSONB,
    literature         JSONB,
    teaching_language  JSONB,

    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_modules_study_program
        FOREIGN KEY (study_program_id)
        REFERENCES study_programs(id)
        ON DELETE CASCADE,

    -- Prevent duplicate module titles within one study program
    CONSTRAINT uq_modules_program_title
        UNIQUE (study_program_id, title)
);

CREATE TABLE study_program_favourites (
    user_id             UUID NOT NULL,
    study_program_id    UUID NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, study_program_id),

    CONSTRAINT fk_study_program_favourites_program
        FOREIGN KEY (study_program_id)
        REFERENCES study_programs(id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_modules_study_program
    ON modules(study_program_id);

CREATE INDEX idx_modules_semester
    ON modules(semester);

CREATE INDEX idx_modules_content_gin
    ON modules USING gin(content);

CREATE INDEX idx_modules_learning_outcomes_gin
    ON modules USING gin(learning_outcomes);

CREATE INDEX idx_modules_teaching_method_gin
    ON modules USING gin(teaching_method);

CREATE INDEX idx_modules_exam_method_gin
    ON modules USING gin(exam_method);

CREATE INDEX idx_modules_literature_gin
    ON modules USING gin(literature);

CREATE INDEX idx_modules_teaching_language_gin
    ON modules USING gin(teaching_language);

CREATE INDEX idx_study_program_favourites_program
    ON study_program_favourites (study_program_id);
