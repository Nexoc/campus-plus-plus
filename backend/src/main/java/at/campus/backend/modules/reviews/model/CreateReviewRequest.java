package at.campus.backend.modules.reviews.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * DTO for creating a new review.
 * Used in POST /api/reviews.
 */
public class CreateReviewRequest {

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

    // Constructors

    public CreateReviewRequest() {
    }

    // Getters and Setters

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
}
