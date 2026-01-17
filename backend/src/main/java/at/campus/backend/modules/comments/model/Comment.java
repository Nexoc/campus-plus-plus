package at.campus.backend.modules.comments.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comment domain model.
 *
 * Represents a comment on a post in a discussion thread.
 */
public class Comment {

    private UUID id;
    private UUID postId;
    private UUID userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors

    public Comment() {
    }

    public Comment(UUID id, UUID postId, UUID userId, String content) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
