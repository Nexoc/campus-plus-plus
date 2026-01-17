package at.campus.backend.modules.threads.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thread domain model.
 *
 * Represents a discussion thread for a course.
 */
public class Thread {

    private UUID id;
    private UUID courseId;
    private String title;
    private String content;
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdAt;

    // Constructors

    public Thread() {
    }

    public Thread(UUID id, UUID courseId, String title, UUID createdBy) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.createdBy = createdBy;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
