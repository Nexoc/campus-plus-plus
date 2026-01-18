-- =====================================================
-- V3__courses.sql
-- =====================================================
-- CREATE TABLE only (plus indexes/comments). No ALTER TABLE.
-- =====================================================

SET search_path TO app;

CREATE TABLE courses (
    id                UUID PRIMARY KEY,

    module_id         UUID,
    study_program_id  UUID,

    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    ects              INTEGER NOT NULL,
    abbreviation      VARCHAR(50),
    language          VARCHAR(50) NOT NULL,

    -- Additional fields from scraped data
    sws               NUMERIC(3,1),
    semester          INTEGER,
    kind              VARCHAR(50),

    details_html      TEXT,
    content           JSONB,
    learning_outcomes JSONB,
    teaching_method   JSONB,
    exam_method       JSONB,
    literature        JSONB,
    teaching_language JSONB,

    source_url        VARCHAR(500),

    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_courses_ects_non_negative
        CHECK (ects >= 0),

    CONSTRAINT fk_courses_module
        FOREIGN KEY (module_id)
        REFERENCES modules(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_courses_study_program
        FOREIGN KEY (study_program_id)
        REFERENCES study_programs(id)
        ON DELETE SET NULL,

    -- Prevent duplicate course titles within one module
    CONSTRAINT uq_courses_module_title
        UNIQUE (module_id, title)
);

CREATE TABLE study_program_courses (
    study_program_id UUID NOT NULL,
    course_id        UUID NOT NULL,

    created_at       TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_study_program_courses
        PRIMARY KEY (study_program_id, course_id),

    CONSTRAINT fk_spc_study_program
        FOREIGN KEY (study_program_id)
        REFERENCES study_programs(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_spc_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

CREATE TABLE course_suggestions (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,

    title       VARCHAR(255) NOT NULL,
    description TEXT,

    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_course_suggestions_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),

    CONSTRAINT uq_course_suggestions_user_title
        UNIQUE (user_id, title)
);

CREATE TABLE course_materials (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    uploader_id UUID NOT NULL,

    title VARCHAR(255),
    description TEXT,

    original_filename VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,

    storage_key VARCHAR(500) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_course_materials_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================

CREATE INDEX idx_courses_semester
    ON courses(semester);

CREATE INDEX idx_courses_kind
    ON courses(kind);

CREATE INDEX idx_courses_module_id
    ON courses(module_id);

CREATE INDEX idx_courses_study_program
    ON courses(study_program_id);

CREATE INDEX idx_courses_content_gin
    ON courses USING gin(content);

CREATE INDEX idx_courses_learning_outcomes_gin
    ON courses USING gin(learning_outcomes);

CREATE INDEX idx_courses_teaching_method_gin
    ON courses USING gin(teaching_method);

CREATE INDEX idx_courses_exam_method_gin
    ON courses USING gin(exam_method);

CREATE INDEX idx_courses_literature_gin
    ON courses USING gin(literature);

CREATE INDEX idx_courses_teaching_language_gin
    ON courses USING gin(teaching_language);

CREATE INDEX idx_spc_study_program
    ON study_program_courses (study_program_id);

CREATE INDEX idx_spc_course
    ON study_program_courses (course_id);

CREATE INDEX idx_course_suggestions_status
    ON course_suggestions (status);

CREATE INDEX idx_course_suggestions_user
    ON course_suggestions (user_id);

CREATE INDEX idx_course_materials_course_id
    ON course_materials(course_id);

CREATE INDEX idx_course_materials_uploader_id
    ON course_materials(uploader_id);

CREATE INDEX idx_course_materials_created_at
    ON course_materials(created_at);

CREATE INDEX idx_course_materials_course_created_at
    ON course_materials(course_id, created_at);
