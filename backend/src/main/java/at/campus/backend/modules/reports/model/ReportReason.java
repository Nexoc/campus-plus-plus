package at.campus.backend.modules.reports.model;

/**
 * Enumeration of predefined report reasons.
 *
 * Purpose:
 * - Provide structured categorization of reports
 * - Ensure consistency across the system
 * - Help moderators quickly understand report types
 *
 * Usage:
 * - Users must select one of these reasons when reporting content
 * - Backend validates that the reason is from this list
 */
public enum ReportReason {
    /**
     * Content is spam or advertising
     */
    SPAM,

    /**
     * Content is offensive or harassing
     */
    OFFENSIVE,

    /**
     * Content contains inappropriate language
     */
    INAPPROPRIATE_LANGUAGE,

    /**
     * Content contains misleading or false information
     */
    MISLEADING_INFORMATION,

    /**
     * Other reason (user should provide details in comment)
     */
    OTHER
}
