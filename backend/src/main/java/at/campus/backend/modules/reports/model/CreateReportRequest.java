package at.campus.backend.modules.reports.model;

import java.util.UUID;

/**
 * Request DTO for creating a report.
 *
 * Fields:
 * - targetType: Type of content being reported (REVIEW, POST)
 * - targetId: ID of the content being reported
 * - reason: Predefined reason from ReportReason enum
 * - comment: Optional free-text comment providing additional context
 */
public class CreateReportRequest {

    private String targetType;  // "REVIEW", "POST"
    private UUID targetId;
    private ReportReason reason;
    private String comment;     // Optional additional context

    // Constructors
    public CreateReportRequest() {}

    public CreateReportRequest(String targetType, UUID targetId, ReportReason reason) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
    }

    public CreateReportRequest(String targetType, UUID targetId, ReportReason reason, String comment) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.comment = comment;
    }

    // Getters and setters
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public ReportReason getReason() { return reason; }
    public void setReason(ReportReason reason) { this.reason = reason; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
