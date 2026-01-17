-- V17: Add comment column to reports table
-- This separates user comments from moderator notes

ALTER TABLE app.reports
    ADD COLUMN IF NOT EXISTS comment TEXT;

COMMENT ON COLUMN app.reports.comment IS 'Optional comment from the user reporting the content';
