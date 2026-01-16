-- =====================================================
-- V13__reviews_moderation_flags.sql
-- =====================================================
-- Add moderation flag columns to reviews table
-- =====================================================

SET search_path TO app;

-- Add moderation flag columns to reviews
ALTER TABLE reviews
    ADD COLUMN moderation_flagged BOOLEAN DEFAULT false,
    ADD COLUMN moderation_reason TEXT;

-- Create index for efficient filtering of flagged reviews
CREATE INDEX idx_reviews_moderation_flagged
    ON reviews (moderation_flagged);

-- =====================================================
-- End of V13
-- =====================================================
