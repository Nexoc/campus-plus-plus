package at.campus.backend.modules.reports.model;

import java.util.UUID;

/**
 * Request DTO for creating a report.
 */
public class CreateReportRequest {

    private String targetType;  // "review", "post", "thread"
    private UUID targetId;
    private String reason;

    // Constructors
    public CreateReportRequest() {}

    public CreateReportRequest(String targetType, UUID targetId, String reason) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
    }

    // Getters and setters
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
