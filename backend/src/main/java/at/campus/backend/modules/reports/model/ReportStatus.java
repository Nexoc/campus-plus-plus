package at.campus.backend.modules.reports.model;

/**
 * Report status enumeration.
 *
 * PENDING: Report awaiting moderator review
 * RESOLVED: Moderator has taken action (content removed, user warned, etc.)
 * REJECTED: Moderator determined the report was unfounded
 * EDITED: Moderator has edited the review content to fix issues
 */
public enum ReportStatus {
    PENDING,
    RESOLVED,
    REJECTED,
    EDITED
}
