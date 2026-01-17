package at.campus.backend.modules.reports.model;

/**
 * Moderation actions that can be taken on a reported review.
 *
 * KEEP_VISIBLE: Review is acceptable, no action taken
 * EDIT: Moderator edits the review to fix problematic content
 * DELETE: Review is permanently removed from the system
 */
public enum ModerationAction {
    /**
     * Keep the review visible - report was unfounded or review is acceptable
     */
    KEEP_VISIBLE,
    
    /**
     * Edit the review content to fix problematic parts
     */
    EDIT,
    
    /**
     * Delete the review permanently - severe violation
     */
    DELETE
}
