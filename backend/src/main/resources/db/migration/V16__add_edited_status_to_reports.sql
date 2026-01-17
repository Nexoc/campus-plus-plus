-- =====================================================
-- V16: Add EDITED status to reports check constraint
-- =====================================================
-- Allows moderators to edit review content, tracked with EDITED status
-- =====================================================

SET search_path TO app;

-- Drop the existing constraint
ALTER TABLE reports DROP CONSTRAINT IF EXISTS chk_reports_status;

-- Add updated constraint with EDITED status
ALTER TABLE reports
ADD CONSTRAINT chk_reports_status
CHECK (status IN ('PENDING','RESOLVED','REJECTED','EDITED'));
