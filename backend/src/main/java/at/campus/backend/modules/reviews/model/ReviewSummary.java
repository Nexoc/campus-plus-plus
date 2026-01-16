package at.campus.backend.modules.reviews.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Summary of reviews for a course.
 * Contains aggregated statistics like average rating and review count.
 */
public class ReviewSummary {

    @JsonProperty("averageRating")
    private Double averageRating;

    @JsonProperty("reviewCount")
    private Integer reviewCount;

    // Constructors

    public ReviewSummary() {
    }

    public ReviewSummary(Double averageRating, Integer reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    // Getters and Setters

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
