-- V18

SET search_path TO app;

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

CREATE INDEX idx_course_materials_course_id
    ON course_materials(course_id);

CREATE INDEX idx_course_materials_uploader_id
    ON course_materials(uploader_id);

CREATE INDEX idx_course_materials_created_at
    ON course_materials(created_at);

-- for sorting by data
CREATE INDEX idx_course_materials_course_created_at
    ON course_materials(course_id, created_at);
