package at.campus.backend.modules.reports.model;

/**
 * Request DTO for updating a report's status.
 */
public class UpdateReportStatusRequest {

    private String status;           // "PENDING", "RESOLVED", or "REJECTED"
    private String moderatorNotes;   // Optional notes from moderator

    // Constructors
    public UpdateReportStatusRequest() {}

    public UpdateReportStatusRequest(String status, String moderatorNotes) {
        this.status = status;
        this.moderatorNotes = moderatorNotes;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModeratorNotes() { return moderatorNotes; }
    public void setModeratorNotes(String moderatorNotes) { this.moderatorNotes = moderatorNotes; }
}
