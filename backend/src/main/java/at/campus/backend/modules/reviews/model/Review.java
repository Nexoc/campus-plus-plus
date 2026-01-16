package at.campus.backend.modules.reviews.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Review domain model.
 *
 * Represents user feedback for a course.
 */
public class Review {

    private UUID id;
    private UUID userId;
    private UUID courseId;

    private Integer rating;
    private Integer difficulty;
    private Integer workload;
    private Integer satisfaction;

    private String priorRequirements;
    private String examInfo;
    private String text;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean moderationFlagged;
    private String moderationReason;

    // Constructors

    public Review() {
    }

    public Review(UUID id, UUID userId, UUID courseId, Integer rating) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getWorkload() {
        return workload;
    }

    public void setWorkload(Integer workload) {
        this.workload = workload;
    }

    public Integer getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getPriorRequirements() {
        return priorRequirements;
    }

    public void setPriorRequirements(String priorRequirements) {
        this.priorRequirements = priorRequirements;
    }

    public String getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(String examInfo) {
        this.examInfo = examInfo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isModerationFlagged() {
        return moderationFlagged;
    }

    public void setModerationFlagged(boolean moderationFlagged) {
        this.moderationFlagged = moderationFlagged;
    }

    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }
}
