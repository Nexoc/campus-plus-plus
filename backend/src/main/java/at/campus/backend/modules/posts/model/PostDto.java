package at.campus.backend.modules.posts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PostDto - Data transfer object for Post.
 *
 * Used in API responses.
 */
public class PostDto {

    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("threadId")
    private UUID threadId;
    
    @JsonProperty("userId")
    private UUID userId;
    
    @JsonProperty("userName")
    private String userName;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;
    
    @JsonProperty("commentCount")
    private Integer commentCount;

    // Constructors

    public PostDto() {
    }

    public PostDto(UUID id, UUID threadId, UUID userId, String userName, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Integer commentCount) {
        this.id = id;
        this.threadId = threadId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
    }

    public static PostDto fromDomain(Post post, Integer commentCount) {
        return new PostDto(
            post.getId(),
            post.getThreadId(),
            post.getUserId(),
            post.getUserName(),
            post.getContent(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            commentCount
        );
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getThreadId() {
        return threadId;
    }

    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
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

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
