-- =====================================================
-- V3: Courses
-- =====================================================
-- Creates the base table for courses.
-- No relations to study programs or other domains
-- are defined in this migration.
-- =====================================================

SET search_path TO app;

-- -----------------------------
-- Modules
-- -----------------------------
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

    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE modules
    ADD CONSTRAINT fk_modules_study_program
    FOREIGN KEY (study_program_id)
    REFERENCES study_programs (id)
    ON DELETE CASCADE;

CREATE INDEX idx_modules_study_program ON modules(study_program_id);
CREATE INDEX idx_modules_semester ON modules(semester);
CREATE INDEX idx_modules_content_gin ON modules USING gin(content);
CREATE INDEX idx_modules_learning_outcomes_gin ON modules USING gin(learning_outcomes);
CREATE INDEX idx_modules_teaching_method_gin ON modules USING gin(teaching_method);
CREATE INDEX idx_modules_exam_method_gin ON modules USING gin(exam_method);
CREATE INDEX idx_modules_literature_gin ON modules USING gin(literature);
CREATE INDEX idx_modules_teaching_language_gin ON modules USING gin(teaching_language);

-- -----------------------------
-- Courses
-- -----------------------------
CREATE TABLE courses (
    id              UUID PRIMARY KEY,
    module_id       UUID,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    ects            INTEGER NOT NULL,
    abbreviation    VARCHAR(50),
    language        VARCHAR(50) NOT NULL,
    
    -- Additional fields from scraped data
    sws             DECIMAL(3,1),           -- Semesterwochenstunden
    semester        INTEGER,                 -- which semester (1-6)
    kind            VARCHAR(50),            -- 'course', 'module', 'exam', etc.
    details_html    TEXT,                   -- raw HTML content
    content           JSONB,
    learning_outcomes JSONB,
    teaching_method   JSONB,
    exam_method       JSONB,
    literature        JSONB,
    teaching_language JSONB,
    source_url      VARCHAR(500),           -- original URL from scraping

    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -----------------------------
-- Constraints
-- -----------------------------
ALTER TABLE courses
ADD CONSTRAINT chk_courses_ects_non_negative
CHECK (ects >= 0);

-- module relation
ALTER TABLE courses
    ADD CONSTRAINT fk_courses_module
    FOREIGN KEY (module_id)
    REFERENCES modules (id)
    ON DELETE SET NULL;

-- -----------------------------
-- Indexes
-- -----------------------------
CREATE INDEX idx_courses_semester ON courses(semester);
CREATE INDEX idx_courses_kind ON courses(kind);
CREATE INDEX idx_courses_module_id ON courses(module_id);
CREATE INDEX idx_courses_content_gin ON courses USING gin(content);
CREATE INDEX idx_courses_learning_outcomes_gin ON courses USING gin(learning_outcomes);
CREATE INDEX idx_courses_teaching_method_gin ON courses USING gin(teaching_method);
CREATE INDEX idx_courses_exam_method_gin ON courses USING gin(exam_method);
CREATE INDEX idx_courses_literature_gin ON courses USING gin(literature);
CREATE INDEX idx_courses_teaching_language_gin ON courses USING gin(teaching_language);

-- =====================================================
-- End of V3
-- =====================================================
