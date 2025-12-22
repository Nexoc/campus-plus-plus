-- =========================
-- Schema initialization
-- =========================

CREATE SCHEMA IF NOT EXISTS app;

-- =========================
-- Projects table
-- =========================

CREATE TABLE app.projects (
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    owner_id     UUID NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =========================
-- Indexes
-- =========================

CREATE INDEX idx_projects_owner_id ON app.projects (owner_id);
