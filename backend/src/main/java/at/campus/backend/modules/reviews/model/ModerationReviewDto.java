package at.campus.backend.modules.reviews.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Moderation DTO = ReviewDto + enriched fields.
 * Enrichment is done server-side (DB lookup).
 */
public class ModerationReviewDto extends ReviewDto {

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("courseTitle")
    private String courseTitle;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
}
