package at.campus.backend.modules.favourites.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a user's favourite course.
 *
 * Responsibilities:
 * - Represents a user's favourite course
 * - Core business entity (no dependencies on API or persistence layers)
 *
 * IMPORTANT:
 * - Domain model contains NO annotations
 * - Domain model is framework-agnostic
 */
public class Favourite {

    private final UUID userId;
    private final UUID courseId;
    private final LocalDateTime createdAt;

    // Course details for display purposes
    private String courseTitle;
    private String courseDescription;
    private Integer courseEcts;

    // -------------------------
    // Constructor
    // -------------------------
    public Favourite(UUID userId, UUID courseId, LocalDateTime createdAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.createdAt = createdAt;
    }

    // -------------------------
    // Getters
    // -------------------------
    public UUID getUserId() {
        return userId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public Integer getCourseEcts() {
        return courseEcts;
    }

    // -------------------------
    // Setters (for enrichment)
    // -------------------------
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public void setCourseEcts(Integer courseEcts) {
        this.courseEcts = courseEcts;
    }
}
