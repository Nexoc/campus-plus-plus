-- =====================================================
-- V17: Add created_by to threads and posts
-- =====================================================
-- Add user tracking to threads and posts.
-- =====================================================

SET search_path TO app;

-- Add created_by column to threads table (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'app' 
        AND table_name = 'threads' 
        AND column_name = 'created_by'
    ) THEN
        ALTER TABLE threads ADD COLUMN created_by UUID;
    END IF;
END $$;

-- Create index on created_by (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE schemaname = 'app' 
        AND tablename = 'threads' 
        AND indexname = 'idx_threads_created_by'
    ) THEN
        CREATE INDEX idx_threads_created_by ON threads (created_by);
    END IF;
END $$;
