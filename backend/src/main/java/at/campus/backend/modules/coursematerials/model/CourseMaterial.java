package at.campus.backend.modules.coursematerials.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CourseMaterial domain model.
 *
 * Represents an uploaded file (PDF or image) linked to a course.
 *
 * Storage model:
 * - Metadata in Postgres (app.course_materials)
 * - File bytes on disk (/data/course-materials)
 * - storageKey bridges DB <-> filesystem
 */
public class CourseMaterial {

    private UUID id;
    private UUID courseId;
    private UUID uploaderId;

    private String title;
    private String description;

    private String originalFilename;
    private String contentType;
    private long sizeBytes;

    private String storageKey;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }

    public UUID getUploaderId() { return uploaderId; }
    public void setUploaderId(UUID uploaderId) { this.uploaderId = uploaderId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
