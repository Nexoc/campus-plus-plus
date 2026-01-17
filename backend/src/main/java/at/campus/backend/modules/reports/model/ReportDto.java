package at.campus.backend.modules.reports.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Report.
 * Used in API responses to expose report information safely.
 */
public class ReportDto {

    private UUID id;
    private String targetType;
    private UUID targetId;
    private UUID userId;
    private String reason;
    private String comment;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private String moderatorNotes;

    // Constructors
    public ReportDto() {}

    public ReportDto(Report report) {
        this.id = report.getId();
        this.targetType = report.getTargetType();
        this.targetId = report.getTargetId();
        this.userId = report.getUserId();
        this.reason = report.getReason() != null ? report.getReason().toString() : null;
        this.comment = report.getComment();
        this.status = report.getStatus().toString();
        this.createdAt = report.getCreatedAt();
        this.resolvedAt = report.getResolvedAt();
        this.moderatorNotes = report.getModeratorNotes();
    }

    // Factory method
    public static ReportDto fromDomain(Report report) {
        return new ReportDto(report);
    }

    // Getters
    public UUID getId() { return id; }
    public String getTargetType() { return targetType; }
    public UUID getTargetId() { return targetId; }
    public UUID getUserId() { return userId; }
    public String getReason() { return reason; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public String getModeratorNotes() { return moderatorNotes; }
}
