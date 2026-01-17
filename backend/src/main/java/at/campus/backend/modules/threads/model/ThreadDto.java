package at.campus.backend.modules.threads.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ThreadDto - Data transfer object for Thread.
 *
 * Used in API responses.
 */
public class ThreadDto {

    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("courseId")
    private UUID courseId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("createdBy")
    private UUID createdBy;
    
    @JsonProperty("createdByName")
    private String createdByName;
    
    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    @JsonProperty("postCount")
    private Integer postCount;

    // Constructors

    public ThreadDto() {
    }

    public ThreadDto(UUID id, UUID courseId, String title, String content, UUID createdBy, String createdByName, LocalDateTime createdAt, Integer postCount) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.postCount = postCount;
    }

    public static ThreadDto fromDomain(Thread thread, Integer postCount) {
        return new ThreadDto(
            thread.getId(),
            thread.getCourseId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCreatedBy(),
            thread.getCreatedByName(),
            thread.getCreatedAt(),
            postCount
        );
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }
}
