package at.campus.backend.modules.reports.model;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for resolving a report with a moderation decision.
 */
public class ResolveReportRequest {

    @NotNull(message = "Moderation action is required")
    private ModerationAction action;

    private String moderatorNotes;
    
    private String editedReviewText;  // For EDIT action - the new review text

    // Constructors
    public ResolveReportRequest() {}

    public ResolveReportRequest(ModerationAction action, String moderatorNotes) {
        this.action = action;
        this.moderatorNotes = moderatorNotes;
    }

    // Getters and setters
    public ModerationAction getAction() {
        return action;
    }

    public void setAction(ModerationAction action) {
        this.action = action;
    }

    public String getModeratorNotes() {
        return moderatorNotes;
    }

    public void setModeratorNotes(String moderatorNotes) {
        this.moderatorNotes = moderatorNotes;
    }

    public String getEditedReviewText() {
        return editedReviewText;
    }

    public void setEditedReviewText(String editedReviewText) {
        this.editedReviewText = editedReviewText;
    }
}
