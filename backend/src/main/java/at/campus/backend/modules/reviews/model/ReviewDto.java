package at.campus.backend.modules.reviews.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Review.
 * Used for API requests and responses.
 */
public class ReviewDto {

    @JsonProperty("reviewId")
    private UUID id;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("courseId")
    private UUID courseId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("difficulty")
    private Integer difficulty;

    @JsonProperty("workload")
    private Integer workload;

    @JsonProperty("satisfaction")
    private Integer satisfaction;

    @JsonProperty("priorRequirements")
    private String priorRequirements;

    @JsonProperty("examInfo")
    private String examInfo;

    @JsonProperty("text")
    private String text;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("isModerationFlagged")
    private boolean moderationFlagged;

    @JsonProperty("moderationReason")
    private String moderationReason;

    // Constructors

    public ReviewDto() {
    }

    // Factory method: Domain to DTO
    public static ReviewDto fromDomain(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.id = review.getId();
        dto.userId = review.getUserId();
        dto.courseId = review.getCourseId();
        dto.rating = review.getRating();
        dto.difficulty = review.getDifficulty();
        dto.workload = review.getWorkload();
        dto.satisfaction = review.getSatisfaction();
        dto.priorRequirements = review.getPriorRequirements();
        dto.examInfo = review.getExamInfo();
        dto.text = review.getText();
        dto.createdAt = review.getCreatedAt();
        dto.updatedAt = review.getUpdatedAt();
        dto.moderationFlagged = review.isModerationFlagged();
        dto.moderationReason = review.getModerationReason();
        return dto;
    }

    // Conversion: DTO to Domain
    public Review toDomain() {
        Review review = new Review();
        review.setId(this.id);
        review.setUserId(this.userId);
        review.setCourseId(this.courseId);
        review.setRating(this.rating);
        review.setDifficulty(this.difficulty);
        review.setWorkload(this.workload);
        review.setSatisfaction(this.satisfaction);
        review.setPriorRequirements(this.priorRequirements);
        review.setExamInfo(this.examInfo);
        review.setText(this.text);
        review.setCreatedAt(this.createdAt);
        review.setUpdatedAt(this.updatedAt);
        review.setModerationFlagged(this.moderationFlagged);
        review.setModerationReason(this.moderationReason);
        return review;
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
