package at.campus.backend.modules.reports.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Report entity representing user-submitted reports for inappropriate content.
 *
 * Purpose:
 * - Allow users to report inappropriate reviews, posts, threads, etc.
 * - Provide moderators with a way to manage and resolve reports
 *
 * Status flow:
 * - PENDING: Report just created, awaiting review
 * - RESOLVED: Moderator has addressed the report (content removed, etc.)
 * - REJECTED: Moderator determined the report was unfounded
 */
public class Report {

    private UUID id;
    private String targetType;      // "review", "post", "thread"
    private UUID targetId;
    private UUID userId;            // Who submitted the report
    private String reason;
    private ReportStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private String moderatorNotes;  // Notes from the moderator

    // Constructors
    public Report() {}

    public Report(String targetType, UUID targetId, UUID userId, String reason) {
        this.id = UUID.randomUUID();
        this.targetType = targetType;
        this.targetId = targetId;
        this.userId = userId;
        this.reason = reason;
        this.status = ReportStatus.PENDING;
        this.createdAt = OffsetDateTime.now();
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getModeratorNotes() { return moderatorNotes; }
    public void setModeratorNotes(String moderatorNotes) { this.moderatorNotes = moderatorNotes; }
}
