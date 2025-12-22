package at.campus.backend.modules.courses.model;

/**
 * Domain model for Course.
 *
 * This class represents a course as used by business logic.
 * It is NOT a JPA entity.
 */
public class Course {

    private final String courseId;
    private final String title;
    private final String description;
    private final int ects;
    private final String abbreviation;
    private final String language;

    public Course(
            String courseId,
            String title,
            String description,
            int ects,
            String abbreviation,
            String language
    ) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.ects = ects;
        this.abbreviation = abbreviation;
        this.language = language;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getEcts() {
        return ects;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getLanguage() {
        return language;
    }
}
