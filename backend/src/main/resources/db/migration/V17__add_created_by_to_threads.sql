-- =====================================================
-- V17: Add created_by to threads and posts
-- =====================================================
-- Add user tracking to threads and posts.
-- =====================================================

SET search_path TO app;

-- Add created_by column to threads table
ALTER TABLE threads
ADD COLUMN created_by UUID;

-- Create index on created_by
CREATE INDEX idx_threads_created_by ON threads (created_by);
