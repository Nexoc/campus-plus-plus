package at.campus.backend.modules.reviews.model;

public class ModerationReviewDto extends ReviewDto {

    private String userName;
    private String courseTitle;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}
