package at.campus.backend.modules.reports.model;

import at.campus.backend.modules.reviews.model.Review;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for moderation view including report details and target review summary.
 */
public class ModerationReportDto {

    private UUID reportId;
    private String targetType;
    private UUID targetId;
    private String reason;
    private String comment;
    private String status;
    private OffsetDateTime createdAt;
    private String moderatorNotes;
    
    // Reporter user fields
    private UUID userId;
    private String userName;
    
    // Review summary fields
    private UUID reviewId;
    private Integer rating;
    private String reviewText;
    private UUID courseId;
    private String courseName;
    private String reviewerNickname;
    private boolean moderationFlagged;

    // Constructors
    public ModerationReportDto() {}

    public ModerationReportDto(Report report, Review review, String userName, String courseName, String reviewerNickname) {
        this.reportId = report.getId();
        this.targetType = report.getTargetType();
        this.targetId = report.getTargetId();
        this.reason = report.getReason() != null ? report.getReason().name() : null;
        this.comment = report.getComment();
        this.status = report.getStatus() != null ? report.getStatus().name() : null;
        this.createdAt = report.getCreatedAt();
        this.moderatorNotes = report.getModeratorNotes();
        
        // User info
        this.userId = report.getUserId();
        this.userName = userName;
        
        if (review != null) {
            this.reviewId = review.getId();
            this.rating = review.getRating();
            this.reviewText = review.getText();
            this.courseId = review.getCourseId();
            this.courseName = courseName;
            this.reviewerNickname = reviewerNickname;
            this.moderationFlagged = review.isModerationFlagged();
        }
    }

    // Getters and setters
    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getModeratorNotes() {
        return moderatorNotes;
    }

    public void setModeratorNotes(String moderatorNotes) {
        this.moderatorNotes = moderatorNotes;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getReviewerNickname() {
        return reviewerNickname;
    }

    public void setReviewerNickname(String reviewerNickname) {
        this.reviewerNickname = reviewerNickname;
    }

    public boolean isModerationFlagged() {
        return moderationFlagged;
    }

    public void setModerationFlagged(boolean moderationFlagged) {
        this.moderationFlagged = moderationFlagged;
    }
}
