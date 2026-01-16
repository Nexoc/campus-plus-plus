-- =====================================================
-- V9: Reports
-- =====================================================
-- Reports for posts and reviews.
-- =====================================================

SET search_path TO app;

CREATE TABLE reports (
    id               UUID PRIMARY KEY,
    user_id          UUID,

    target_type      VARCHAR(20) NOT NULL,
    target_id        UUID NOT NULL,

    reason           TEXT NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at      TIMESTAMP,
    moderator_notes  TEXT
);

-- Constraints
ALTER TABLE reports
ADD CONSTRAINT chk_reports_status
CHECK (status IN ('PENDING','RESOLVED','REJECTED'));

ALTER TABLE reports
ADD CONSTRAINT chk_reports_target_type
CHECK (target_type IN ('POST','REVIEW'));

-- Indexes
CREATE INDEX idx_reports_target ON reports (target_type, target_id);
CREATE INDEX idx_reports_status ON reports (status);