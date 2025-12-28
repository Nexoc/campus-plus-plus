package at.campus.backend.modules.courses.model;

import java.util.UUID;

/**
 * Domain model for Course.
 *
 * This class represents a course as used by business logic.
 * It is NOT a JPA entity.
 */
public class Course {

    private final UUID courseId;
    private final String title;
    private final String description;
    private final int ects;
    private final String language;
    private final java.math.BigDecimal sws;
    private final Integer semester;
    private final String kind;
    private final String detailsHtml;
    private final Object content;
    private final Object learningOutcomes;
    private final Object teachingMethod;
    private final Object examMethod;
    private final Object literature;
    private final Object teachingLanguage;
    private final String sourceUrl;

    public Course(
            UUID courseId,
            String title,
            String description,
            int ects,
            String language,
            java.math.BigDecimal sws,
            Integer semester,
            String kind,
            String detailsHtml,
                Object content,
                Object learningOutcomes,
                Object teachingMethod,
                Object examMethod,
                Object literature,
                Object teachingLanguage,
            String sourceUrl
    ) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.ects = ects;
        this.language = language;
        this.sws = sws;
        this.semester = semester;
        this.kind = kind;
        this.detailsHtml = detailsHtml;
        this.content = content;
        this.learningOutcomes = learningOutcomes;
        this.teachingMethod = teachingMethod;
        this.examMethod = examMethod;
        this.literature = literature;
        this.teachingLanguage = teachingLanguage;
        this.sourceUrl = sourceUrl;
    }

    public UUID getCourseId() {
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

    public String getLanguage() {
        return language;
    }

    public java.math.BigDecimal getSws() { return sws; }

    public Integer getSemester() { return semester; }

    public String getKind() { return kind; }

    public String getDetailsHtml() { return detailsHtml; }

    public Object getContent() { return content; }

    public Object getLearningOutcomes() { return learningOutcomes; }

    public Object getTeachingMethod() { return teachingMethod; }

    public Object getExamMethod() { return examMethod; }

    public Object getLiterature() { return literature; }

    public Object getTeachingLanguage() { return teachingLanguage; }

    public String getSourceUrl() { return sourceUrl; }

}
